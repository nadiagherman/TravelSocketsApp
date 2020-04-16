package repository;


import entities.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graalvm.compiler.core.phases.MidTier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class TicketJdbcRepository implements CrudRepository<Integer,Ticket>{

    private static final Logger logger= LogManager.getLogger(TicketJdbcRepository.class);
    private JdbcUtils dbUtils;

    public TicketJdbcRepository(Properties properties){
        logger.info("initializing TicketRepo with with properties: {}  ", properties);
        dbUtils=new JdbcUtils(properties);
    }


    public Ticket save(Ticket ticket){
        logger.traceEntry("saving ticket {} " ,ticket);
        Connection con=dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("insert into Ticket values (?,?,?,?,?,?,?)")){
            int idTicket= size()+1;
            preStmt.setInt(1, idTicket);
            preStmt.setInt(2,ticket.getAccountId());
            preStmt.setInt(3,ticket.getFlightId());
            preStmt.setString(4,ticket.getClientName());
            preStmt.setString(5,ticket.getTourists());
            preStmt.setString(6,ticket.getClientAdress());
            preStmt.setInt(7,ticket.getNrSeats());
            int result=preStmt.executeUpdate();
            ticket.setId(idTicket);
            logger.traceExit(ticket);
            return ticket;
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        return null;

    }

    @Override
    public Ticket delete(Integer integer) {
        return null;
    }

    @Override
    public int size() {
        return (int)StreamSupport.stream(findAll().spliterator(),false).count();
    }

    @Override
    public Ticket findOne(Integer integer) {
        return null;
    }

    public Iterable<Ticket> findAll(){

        logger.traceEntry("returning all acounts ...");

        Connection conn= dbUtils.getConnection();
        List<Ticket> tickets= new ArrayList<>();
        try(PreparedStatement preparedStatement=conn.prepareStatement("select * from Ticket")){
            try(ResultSet result= preparedStatement.executeQuery()){
                while (result.next()){
                    int id=result.getInt("id");
                    int accid=result.getInt("accid");
                    int fid=result.getInt("fid");
                    String client=result.getString("client");
                    String tourists=result.getString("tourists");
                    String adress=result.getString("adress");
                    int nr=result.getInt("nr");

                    Ticket ticket=new Ticket(id,accid,fid,client,tourists,adress,nr);

                    tickets.add(ticket);
                }
            }
        }
        catch (SQLException e){

            logger.error(e);
            System.out.println("Error DB "+e);

        }

        logger.traceExit(tickets);
        return tickets;

    }
}

