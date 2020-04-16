package service;

import dto.AccountDTO;
import dto.FlightDTO;
import dto.TicketDTO;
import entities.Account;
import entities.Flight;
import entities.Ticket;
import net.Request;
import net.RequestType;
import net.Response;
import net.ResponseType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TravelServicesProxy extends BaseServiceProxy implements TravelServices {

    private static final Logger LOGGER = LogManager.getLogger(TravelServicesProxy.class.getName());
    protected TravelObserver travelObserver;

    public TravelServicesProxy(String host, int port) {
        super(host, port);
    }

    @Override
    protected void handleTicketAdded(Response response) {


        TicketDTO ticketDto = (TicketDTO)response.data();
        Ticket t = new Ticket(ticketDto.id, ticketDto.accountDTO.id, ticketDto.flightDTO.id, ticketDto.clientName, ticketDto.tourists,ticketDto.clientAdress, ticketDto.nrSeats);
        travelObserver.ticketInserted(t);
    }

    @Override
    public Account autentification(String name, String password) {
        LOGGER.traceEntry("getting account by username " + name + " and password " + password);
        ensureConnected();
        Request req = new Request.Builder()
                .type(RequestType.LOGIN)
                .data(new AccountDTO(name, password))
                .build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.OK) {
            LOGGER.info("received OK response");
            AccountDTO accountDto = (AccountDTO) response.data();
            if (accountDto == null) {
                LOGGER.info("no account found");
                return null;
            }
            Account a = new Account(accountDto.id, accountDto.name, accountDto.password);
            LOGGER.info("found account " + a);
            return a;
        }
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            LOGGER.info("received ERROR response " + err);
            throw new ServiceException(err);
        }
        LOGGER.traceExit();
        return null;
    }

    @Override
    public void addTicket(String client, String tourists, String clientAddr, int nr, Flight f, Account account) {
        LOGGER.traceEntry("adding booking for client " + client + " to trip " + f + " made by " + account);
        ensureConnected();
        FlightDTO flightDto = new FlightDTO(f.getId(),f.getDestination(),f.getDate(),f.getTime(),f.getAirportName(),f.getAvailableSeats());
        AccountDTO accountDto = new AccountDTO(account.getId(), account.getUserName(), account.getPassword());
        Request req = new Request.Builder()
                .type(RequestType.ADD_TICKET)
                .data(new TicketDTO(client, tourists,clientAddr,nr, accountDto, flightDto))
                .build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.OK) {
            LOGGER.info("received OK response");
            LOGGER.info("booking added");
            return;
        }
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            LOGGER.info("add booking failed received ERROR response " + err);
//            closeConnection();
            throw new ServiceException(err);
        }
    }

    @Override
    public List<Flight> getAllFlights() {
        LOGGER.info("getting all trips");
        ensureConnected();
        Request req = new Request.Builder()
                .type(RequestType.GET_ALL_FLIGHTS)
                .build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.OK) {
            LOGGER.info("getting all trips OK response");
            List<FlightDTO> flights = (ArrayList<FlightDTO>) response.data();
            return flights.stream()
                    .map(f -> new Flight(f.id, f.destination, f.date, f.time, f.airportName, f.availableSeats))
                    .collect(Collectors.toList());
        }
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            LOGGER.warn("getting all trips failed");
//            closeConnection();
            throw new ServiceException(err);
        }
        return null;
    }

    @Override
    public List<Flight> getFlightsbyDestAndDate(String dest, LocalDate date) {
        LOGGER.traceEntry("getting flights with destination " + dest + " and date " + date);
        ensureConnected();
        Request req = new Request.Builder()
                .type(RequestType.SEARCH_FLIGHTS)
                .data(new FlightDTO(dest,date))
                .build();
        sendRequest(req);
        Response response = readResponse();
        LOGGER.info("response for search trips " + response);
        if (response.type() == ResponseType.OK) {
            LOGGER.info("received OK response");
            List<FlightDTO> flights = (ArrayList<FlightDTO>) response.data();
            LOGGER.info("returning searched trips ");
            return flights.stream()
                    .map(f -> new Flight(f.id, f.destination, f.date, f.time, f.airportName, f.availableSeats))
                    .collect(Collectors.toList());
        }
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            LOGGER.info("searching trips failed received ERROR response " + err);
            throw new ServiceException(err);
        }
        LOGGER.traceExit();
        return null;
    }


    @Override
    public void logOut() {
        LOGGER.info("logging out");
//        ensureConnected();
        if (connection == null){
            LOGGER.info("connection already closed");
            return;
        }
        Request req = new Request.Builder().type(RequestType.LOGOUT).build();
        sendRequest(req);
        Response response = readResponse();
        closeConnection();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            LOGGER.warn("logging out failed " + err);
            throw new ServiceException(err);
        }
    }

    @Override
    public void addTravelObserver(TravelObserver observer) {
        travelObserver = observer;
    }

    @Override
    public void removeTravelObserver(TravelObserver observer) {
        travelObserver = null;
    }
}


