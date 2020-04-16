package repository;

import entities.Flight;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class FlightJdbcRepository implements CrudRepository<Integer,Flight> {


    private static final Logger logger= LogManager.getLogger(FlightJdbcRepository.class);
    private JdbcUtils dbUtils;

    public FlightJdbcRepository(Properties properties){
        logger.info("initializing FlightRepo with with properties: {}  ", properties);
        dbUtils=new JdbcUtils(properties);
    }


    public Flight save(Flight flight){
        logger.traceEntry("saving flight {} " ,flight);
        Connection con=dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("insert into Flight values (?,?,?,?,?,?)")){
            preStmt.setInt(1,flight.getId());
            preStmt.setString(2,flight.getDestination());
            preStmt.setString(3,flight.getDate().toString());
            logger.trace("aici adaugam data .... {} ",flight.getDate().toString());
            preStmt.setString(4,flight.getTime().toString());
            preStmt.setString(5,flight.getAirportName());
            preStmt.setInt(6,flight.getAvailableSeats());
            int result=preStmt.executeUpdate();
            logger.traceExit(flight);
            return flight;
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        return null;

    }

    public void update(Flight entity,int nrSeats){
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("update Flight set available=? where id=?")){
            int newNrSeats = entity.getAvailableSeats()- nrSeats;
            preStmt.setInt(1,newNrSeats);
            preStmt.setInt(2,entity.getId());
            preStmt.executeUpdate();
        }catch(SQLException ex){
            System.out.println("Error DB "+ex);
        }

    }

    public Iterable<Flight> findAfterDateAndDestination(String destination, String date){

        logger.traceEntry("searching flights after date {} and destination {}",date ,destination);
        List<Flight> flights= new ArrayList<>();
        Connection con=dbUtils.getConnection();

        try (PreparedStatement preStmt=con.prepareStatement("select * from Flight where destination=? AND date=?")){
            preStmt.setString(1,destination);
            preStmt.setString(2,date);
            try (ResultSet resultSet= preStmt.executeQuery()){
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String dest = resultSet.getString("destination");
                    LocalDate date1 = LocalDate.parse(resultSet.getString("date"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    LocalTime time1 = LocalTime.parse(resultSet.getString("time"), DateTimeFormatter.ISO_TIME);
                    String airp = resultSet.getString("airport");
                    int av = resultSet.getInt("available");
                    Flight flight = new Flight(id, dest, date1, time1, airp, av);
                    flights.add(flight);


                }
            }
        }
        catch (SQLException e){

            logger.error(e);
            System.out.println("Error DB "+e);

        }

        logger.traceExit(flights);
        return flights;

    }
    public Flight findOne(Integer integer) {
        logger.traceEntry("searching for flight with id {} ", integer);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("select * from Flight where id=?")) {
            preStmt.setInt(1, integer);
            try (ResultSet resultSet = preStmt.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String dest = resultSet.getString("destination");
                    LocalDate date = LocalDate.parse(resultSet.getString("date"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    LocalTime time = LocalTime.parse(resultSet.getString("time"), DateTimeFormatter.ISO_TIME);
                    String airp = resultSet.getString("airport");
                    int av = resultSet.getInt("available");
                    Flight flight = new Flight(id, dest, date, time, airp, av);
                    logger.traceExit(flight);
                    return flight;
                }
            }
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        logger.traceExit("No flight found with id {}", integer);

        return null;
    }


    public Flight delete(Integer integer){
        logger.traceEntry("deleting flight with {}",integer);
        Connection con=dbUtils.getConnection();
        Flight f = findOne(integer);
        try(PreparedStatement preStmt=con.prepareStatement("delete from Flight where id=?")){
            preStmt.setInt(1,integer);
            int result=preStmt.executeUpdate();
            logger.traceExit(f);
            return f;
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        return null;

    }

    @Override
    public int size() {
        return 0;
    }

    public Iterable<Flight> findAll(){

        Connection conn= dbUtils.getConnection();
        List<Flight> flights= new ArrayList<>();
        try(PreparedStatement preparedStatement=conn.prepareStatement("select * from Flight")){
            try(ResultSet result= preparedStatement.executeQuery()){
                while (result.next()){
                    int id=result.getInt("id");
                    String destination=result.getString("destination");
                    LocalDate date= LocalDate.parse(result.getString("date"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    LocalTime time=LocalTime.parse(result.getString("time"),DateTimeFormatter.ISO_TIME);
                    String airport=result.getString("airport");
                    int available=result.getInt("available");
                    Flight flight=new Flight(id,destination,date,time,airport,available);
                    flights.add(flight);
                }
            }
        }
        catch (SQLException e){

            logger.error(e);
            System.out.println("Error DB "+e);

        }

        logger.traceExit(flights);
        return flights;

    }


}
