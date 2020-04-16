package service;

import entities.Account;
import entities.Flight;

import java.time.LocalDate;
import java.util.List;

public interface TravelServices {

    Account autentification(String user, String pass);

    void addTicket(String client, String tourists, String clientAddr, int nr, Flight f, Account account);

    List<Flight> getAllFlights();

    List<Flight> getFlightsbyDestAndDate(String dest, LocalDate date);

    void logOut();

    void addTravelObserver(TravelObserver travelObserver);

    void removeTravelObserver(TravelObserver travelObserver);

}

