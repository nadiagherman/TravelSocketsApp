package entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class Flight extends Entity<Integer> implements Serializable {

    /*-destination: String -date: LocalDate -time: LocalTime -airportName: String -availableSeats: int*/

    private String destination;
    private LocalDate date;
    private LocalTime time;
    private String airportName;
    private int availableSeats;

    public Flight(int id,String destination, LocalDate date, LocalTime time, String airportName,int availableSeats) {
        this.id=id;
        this.destination = destination;
        this.date = date;
        this.time = time;
        this.airportName=airportName;
        this.availableSeats = availableSeats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flight flight = (Flight) o;
        return Objects.equals(destination, flight.destination) &&
                Objects.equals(date, flight.date) &&
                Objects.equals(time, flight.time) &&
                Objects.equals(airportName,flight.airportName) &&
                availableSeats == flight.availableSeats;
    }

    @Override
    public int hashCode() {
        return Objects.hash(destination, date, time, airportName, availableSeats);
    }

    public String getAirportName() {
        return airportName;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "destination='" + destination + '\'' +
                ", date=" + date +
                ", time=" + time +
                ", airportName='" + airportName + '\'' +
                ", availableSeats=" + availableSeats +
                ", id=" + id +
                '}';
    }

    public void setAirportName(String airportName) {
        this.airportName = airportName;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }
}
