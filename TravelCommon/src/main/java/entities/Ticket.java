package entities;

import java.io.Serializable;
import java.util.Objects;

public class Ticket extends Entity<Integer> implements Serializable {


    /*-clientName: String -touristsName: String[] -clientAdress: String -nrSeats: int*/
    private int accountId;
    private int flightId;
    private String clientName;
    private String tourists;
    private String clientAdress;
    private int nrSeats;

    public Ticket(Integer id,int accountId,int flightId, String clientName, String tourists, String clientAdress, int nrSeats) {
        this.id=id;
        this.flightId = flightId;
        this.accountId = accountId;
        this.clientName = clientName;
        this.tourists = tourists;
        this.clientAdress = clientAdress;
        this.nrSeats = nrSeats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return flightId == ticket.flightId &&
                accountId == ticket.accountId &&
                nrSeats == ticket.nrSeats &&
                Objects.equals(clientName, ticket.clientName) &&
                Objects.equals(tourists, ticket.tourists) &&
                Objects.equals(clientAdress, ticket.clientAdress);
    }


    @Override
    public String toString() {
        return "Ticket{" +
                "flightId=" + flightId +
                ", accountId=" + accountId +
                ", clientName='" + clientName + '\'' +
                ", tourists='" + tourists + '\'' +
                ", clientAdress='" + clientAdress + '\'' +
                ", nrSeats=" + nrSeats +
                ", id=" + id +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(flightId, accountId, clientName, tourists, clientAdress, nrSeats);
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getTourists() {
        return tourists;
    }

    public void setTourists(String tourists) {
        this.tourists = tourists;
    }

    public String getClientAdress() {
        return clientAdress;
    }

    public void setClientAdress(String clientAdress) {
        this.clientAdress = clientAdress;
    }

    public int getNrSeats() {
        return nrSeats;
    }

    public void setNrSeats(int nrSeats) {
        this.nrSeats = nrSeats;
    }
}
