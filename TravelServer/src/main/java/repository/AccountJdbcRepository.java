package repository;

import entities.Account;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AccountJdbcRepository implements CrudRepository<Integer,Account> {

    private static final Logger logger= LogManager.getLogger(AccountJdbcRepository.class);
    private JdbcUtils dbUtils;

    public AccountJdbcRepository(Properties properties){
        logger.info("initializing AccRepo with with properties: {}  ", properties);
        dbUtils=new JdbcUtils(properties);
    }



    public Account findByUserAndPassword(String username, String password){
        logger.traceEntry("returning account with user {} and pass {}",username,password);

        Connection conn=dbUtils.getConnection();
        try(PreparedStatement preparedStatement=conn.prepareStatement("select * from Account where username=? and password=?")){
            preparedStatement.setString(1,username);
            preparedStatement.setString(2,password);
            try(ResultSet resultSet=preparedStatement.executeQuery()){
                if(resultSet.next()){
                    int id=resultSet.getInt("id");
                    String user=resultSet.getString("username");
                    String pass=resultSet.getString("password");
                    Account ac=new Account(id,user,pass);
                    logger.traceExit(ac);
                    return ac;
                }
            }
        }
        catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        logger.traceExit("no account found");

        return null;

    }

    @Override
    public Account findOne(Integer integer) {
        return null;
    }

    public Iterable<Account> findAll(){
        logger.traceEntry("returning all acounts ...");

        Connection conn= dbUtils.getConnection();
        List<Account> accounts= new ArrayList<>();
        try(PreparedStatement preparedStatement=conn.prepareStatement("select * from Account")){
            try(ResultSet result= preparedStatement.executeQuery()){
                while (result.next()){
                    int id=result.getInt("id");
                    String username=result.getString("username");
                    String password=result.getString("password");
                    Account account=new Account(id,username,password);
                    accounts.add(account);
                }
            }
        }
        catch (SQLException e){

            logger.error(e);
            System.out.println("Error DB "+e);

        }

        logger.traceExit(accounts);
        return accounts;

    }

    @Override
    public Account save(Account account) {
        return null;
    }

    @Override
    public Account delete(Integer integer) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

}
