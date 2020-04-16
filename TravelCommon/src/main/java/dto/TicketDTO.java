package dto;

import java.io.Serializable;

public class TicketDTO implements Serializable {

    public Integer id;
    public String clientName;
    public String tourists;
    public String clientAdress;
    public int nrSeats;
    public AccountDTO accountDTO;
    public FlightDTO flightDTO;

    public TicketDTO(Integer id, String client, String tourists, String clientAdress, int nrSeats, AccountDTO accountDTO, FlightDTO flightDTO){
        this.id= id;
        this.clientName = client;
        this.tourists=tourists;
        this.clientAdress=clientAdress;
        this.nrSeats=nrSeats;
        this.accountDTO = accountDTO;
        this.flightDTO = flightDTO;
    }

    public TicketDTO(String client, String tourists, String clientAdress, int nrSeats, AccountDTO accountDTO, FlightDTO flightDTO){
        this.clientName = client;
        this.tourists=tourists;
        this.clientAdress=clientAdress;
        this.nrSeats=nrSeats;
        this.accountDTO = accountDTO;
        this.flightDTO = flightDTO;
    }

}
