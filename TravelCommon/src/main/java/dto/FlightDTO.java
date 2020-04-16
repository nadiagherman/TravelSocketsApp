package dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class FlightDTO implements Serializable {

    public Integer id;
    public String destination;
    public LocalDate date;
    public LocalTime time;
    public String airportName;
    public int availableSeats;


    public FlightDTO(String destination, LocalDate date){
        this.destination = destination;
        this.date = date;
    }

    public FlightDTO(Integer id,String destination,LocalDate date,LocalTime time,String airportName,int availableSeats){
        this.id=id;
        this.destination=destination;
        this.date=date;
        this.time=time;
        this.airportName=airportName;
        this.availableSeats=availableSeats;
    }

    public FlightDTO(Integer id){
        this.id=id;
    }

    @Override
    public String toString() {
        return "FlightDTO{" +
                "id=" + id +
                ", destination='" + destination + '\'' +
                ", date='" + date + '\'' +
                ", time=" + time +
                ", airport=" + airportName +
                ", available=" + availableSeats +
                '}';
    }




}
