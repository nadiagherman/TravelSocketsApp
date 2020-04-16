import controller.LoginController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import service.TravelServices;

public class Main extends Application {
    private static Logger logger= LogManager.getLogger(Main.class);



    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        logger.info("creating application context");
        ApplicationContext context = new ClassPathXmlApplicationContext("/travelXMLClient.xml");
        TravelServices travelServices = context.getBean(TravelServices.class);

        primaryStage.setTitle("Log in");
        FXMLLoader loader=new FXMLLoader();
        loader.setLocation(getClass().getResource("/loginWindow.fxml"));
        AnchorPane myPane = (AnchorPane) loader.load();
        LoginController ctrl=loader.getController();
        ctrl.setService(travelServices, primaryStage);
        Scene myScene = new Scene(myPane);
        primaryStage.setScene(myScene);


        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                travelServices.logOut();
            }
        });

        primaryStage.show();

    }

}
