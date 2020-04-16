package services;

import entities.Account;
import entities.Flight;
import entities.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.AccountJdbcRepository;
import repository.FlightJdbcRepository;
import repository.TicketJdbcRepository;
import service.ServiceException;
import service.TravelObserver;
import service.TravelServices;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class TravelServicesImpl implements TravelServices{

    private static final Logger logger = LogManager.getLogger(TravelServicesImpl.class.getName());
     private AccountJdbcRepository accountJdbcRepository;
     private FlightJdbcRepository flightJdbcRepository;
     private TicketJdbcRepository ticketJdbcRepository;
    private List<TravelObserver> travelObservers = new ArrayList<>();

     public TravelServicesImpl(AccountJdbcRepository accountJdbcRepository,FlightJdbcRepository flightJdbcRepository, TicketJdbcRepository ticketJdbcRepository){
         this.accountJdbcRepository=accountJdbcRepository;
         this.flightJdbcRepository=flightJdbcRepository;
         this.ticketJdbcRepository=ticketJdbcRepository;
     }

    @Override
    public synchronized Account autentification(String user, String pass) {
        logger.info("getting account by username and password");
        return accountJdbcRepository.findByUserAndPassword(user, pass);
    }

    @Override
    public synchronized void addTicket(String client, String tourists, String clientAddr, int nr, Flight f, Account account) {
        logger.info("adding ticket");
        if (f.getAvailableSeats() < nr) {
            throw new ServiceException("not enough seats left for this flight.");
        }
        Ticket ticket = new Ticket(null, account.getId(), f.getId(), client,tourists,clientAddr,nr);
        Ticket savedTicket = ticketJdbcRepository.save(ticket);
        int newNrSeats = f.getAvailableSeats() - nr;
        flightJdbcRepository.update(f, newNrSeats);
        logger.info("ticket added");
        ExecutorService executor = Executors.newFixedThreadPool(5);
        logger.info("notifying observers");
        executor.execute(() -> {
            travelObservers.forEach(t -> {
                try {
                    t.ticketInserted(savedTicket);
                } catch (Exception e) {
                    logger.warn("Error notifying observer " + e);
                }
            });
        });
        executor.shutdown();
        logger.info("observers notified");
    }

    @Override
    public synchronized List<Flight> getAllFlights() {
        logger.info("getting all flights");
        List<Flight> allFlights = StreamSupport.stream(flightJdbcRepository.findAll().spliterator(),false)
                .collect(Collectors.toList());
        return allFlights;
    }

    @Override
    public synchronized List<Flight> getFlightsbyDestAndDate(String dest, LocalDate date) {
        logger.info("getting all searched flights");
        String dateS = date.toString();
        logger.info(" date searched for is = " + dateS);
        List<Flight> allSFlights = StreamSupport.stream(flightJdbcRepository.findAfterDateAndDestination(dest,dateS).spliterator(),false)
                .collect(Collectors.toList());
        return allSFlights;
    }

    @Override
    public void logOut() {

    }

    @Override
    public synchronized void addTravelObserver(TravelObserver travelObserver) {
         logger.info("adding observer " + travelObserver );
          travelObservers.add(travelObserver);
    }

    @Override
    public synchronized void removeTravelObserver(TravelObserver travelObserver) {
           logger.info("remove observer " + travelObserver);
           travelObservers.remove(travelObserver);
    }
}
