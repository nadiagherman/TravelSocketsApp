package service;


import entities.Ticket;

public interface TravelObserver {
    void ticketInserted(Ticket ticket);
}
