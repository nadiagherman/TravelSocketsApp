package repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JdbcUtils {

    private static final Logger logger= LogManager.getLogger(JdbcUtils.class);
    private Properties jdbcProps;
    private Connection instance=null;


    public JdbcUtils(Properties props){
        jdbcProps=props;
    }

    public Connection getNewConnection(){
        logger.traceEntry();
        // String driver=jdbcProps.getProperty("TestGradle.jdbc.driver");
        String url=jdbcProps.getProperty("mpp.jdbc.url");

        String user=jdbcProps.getProperty("mpp.jdbc.user");
        String pass=jdbcProps.getProperty("mpp.jdbc.pass");

        logger.info("trying to connect to database..... {} ",url);
        Connection conn=null;
        try {
            // Class.forName(driver);
            //  logger.info("Loaded driver ...{}",driver);
            if (user!=null && pass!=null)
                conn= DriverManager.getConnection(url,user,pass);
            else
                conn=DriverManager.getConnection(url);


        } /*catch (ClassNotFoundException e) {
            logger.error(e);
            System.out.println("Error loading driver "+e);
        }*/ catch (SQLException e) {
            logger.error(e);
            System.out.println("Error getting connection "+e);
        }
        return conn;

    }

    public Connection getConnection(){
        logger.traceEntry();
        try {
            if (instance==null || instance.isClosed())
                instance=getNewConnection();

        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB "+e);
        }
        logger.traceExit(instance);
        return instance;
    }

}
