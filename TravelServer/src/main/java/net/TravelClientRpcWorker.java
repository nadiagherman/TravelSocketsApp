package net;

import dto.AccountDTO;
import dto.FlightDTO;
import dto.TicketDTO;
import entities.Account;
import entities.Flight;
import entities.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.TravelObserver;
import service.TravelServices;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;

public class TravelClientRpcWorker implements Runnable, TravelObserver {
    private TravelServices service;
    private Socket connection;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;
    private static final Logger LOGGER = LogManager.getLogger(TravelClientRpcWorker.class.getName());

    public TravelClientRpcWorker(TravelServices travelServices, Socket connection) {
        LOGGER.traceEntry("initializing worker");
        this.service = travelServices;
        this.connection = connection;
        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = true;
        } catch (IOException e) {
            LOGGER.warn("initializing worker failed");
            e.printStackTrace();
        }
    }

    public void run() {
        LOGGER.traceEntry("running worker");
        while (connected) {
            try {
                Object request = input.readObject();
                LOGGER.info("received request " + request);
                Response response = handleRequest((Request) request);
                LOGGER.info("response for request " + response);
                if (response != null) {
                    LOGGER.info("sending response " + response);
                    sendResponse(response);
                }
            } catch (Exception e) {
                if (connected) {
                    LOGGER.warn("reading request failed");
                    e.printStackTrace();
                }
                break;
            }
        }
        try {
            LOGGER.info("closing connection");
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            LOGGER.warn("closing connection failed");
            e.printStackTrace();
        }
    }


    private static Response okResponse = new Response.Builder().type(ResponseType.OK).build();

    private static Response errorResponse = new Response.Builder().type(ResponseType.ERROR).build();

    private Response handleRequest(Request request) {
        LOGGER.info("handling request " + request);
        if (request.type() == RequestType.LOGIN) {
            LOGGER.info("handling LOGIN request");
            AccountDTO accountDto = (AccountDTO) request.data();
            try {
                LOGGER.info("finding account with name " + accountDto.name + "password " + accountDto.password);
                Account a = service.autentification(accountDto.name, accountDto.password);
                LOGGER.info("found account " + a);
                if (a == null) {
                    return okResponse;
                }
                service.addTravelObserver(this);
                return new Response.Builder()
                        .type(ResponseType.OK)
                        .data(new AccountDTO(a.getId(), a.getUserName(), a.getPassword()))
                        .build();
            } catch (Exception e) {
                LOGGER.warn("finding account failed");
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type() == RequestType.GET_ALL_FLIGHTS) {
            LOGGER.info("handling GET_ALL_FLIGHTS request");
            try {
                List<Flight> flights = service.getAllFlights();
                List<FlightDTO> flightDtos = flights.stream()
                        .map(f -> new FlightDTO(f.getId(), f.getDestination(), f.getDate(), f.getTime(), f.getAirportName(), f.getAvailableSeats()))
                        .collect(Collectors.toList());
                LOGGER.info("returning response with flights");
                return new Response.Builder()
                        .type(ResponseType.OK)
                        .data(flightDtos)
                        .build();
            } catch (Exception e) {
                LOGGER.warn("getting all trips failed");
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type() == RequestType.LOGOUT) {
            LOGGER.info("Logout request");
            //service.logOut();
            service.removeTravelObserver(this);
            connected = false;
            return okResponse;
        }
        if (request.type() == RequestType.SEARCH_FLIGHTS) {
            try {
                LOGGER.info("handling SEARCH_FLIGHTS request");
                FlightDTO flightDto = (FlightDTO) request.data();
                List<Flight> flights = service.getFlightsbyDestAndDate(flightDto.destination, flightDto.date);
                LOGGER.info("searching for flights with dest " + flightDto.destination + " and date = " + flightDto.date);
                List<FlightDTO> flightDtos = flights.stream()
                        .map(f -> new FlightDTO(f.getId(), f.getDestination(), f.getDate(), f.getTime(), f.getAirportName(), f.getAvailableSeats()))
                        .collect(Collectors.toList());
                LOGGER.info("returning response with flights searched");
                return new Response.Builder()
                        .type(ResponseType.OK)
                        .data(flightDtos)
                        .build();
            } catch (Exception e) {
                LOGGER.warn("getting searched flights failed");
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type() == RequestType.ADD_TICKET) {
            LOGGER.info("handling ADD_BOOKING request");
            try {
                TicketDTO ticketDto = (TicketDTO) request.data();
                FlightDTO  flightDTO = ticketDto.flightDTO;
                AccountDTO accountDTO= ticketDto.accountDTO;
                service.addTicket(ticketDto.clientName, ticketDto.tourists, ticketDto.clientAdress, ticketDto.nrSeats,
                        new Flight(flightDTO.id, flightDTO.destination, flightDTO.date, flightDTO.time, flightDTO.airportName, flightDTO.availableSeats),
                         new Account(accountDTO.id, accountDTO.name, accountDTO.password));
                LOGGER.info("saved ticket returning ok response");
                return okResponse;
            } catch (Exception ve) {
                LOGGER.warn("saving ticket failed");
                return new Response.Builder().type(ResponseType.ERROR).data(ve.getMessage()).build();
            }
        }
        LOGGER.traceExit();
        return null;
    }

    private void sendResponse(Response response) throws IOException {
        LOGGER.info("sending response to client " + response);
        output.writeObject(response);
        output.flush();
    }

    @Override
    public void ticketInserted(Ticket ticket) {
        LOGGER.info("sending ticket inserted response");
        TicketDTO ticketDto= new TicketDTO(ticket.getId(), ticket.getClientName(), ticket.getTourists(),ticket.getClientAdress(),
                ticket.getNrSeats(),  new AccountDTO(ticket.getAccountId()), new FlightDTO(ticket.getFlightId()));
        Response response = new Response.Builder().type(ResponseType.TICKET_ADDED).data(ticketDto).build();
        try {
            sendResponse(response);
        } catch (IOException e) {
            LOGGER.info("sending response ticket inserted failed");
            e.printStackTrace();
        }
    }
}
