package controller;

import entities.Account;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import service.TravelServices;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {


    private TravelServices travelServices;

    @FXML

    private Stage stage;
    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordTextField;


    public void setService(TravelServices travelService, Stage primaryStage){
        this.travelServices = travelService;
        this.stage= primaryStage;
    }


       /* loginButton.setOnAction(e -> {
            ((Node)e.getSource()).getScene().getWindow().hide();
            String user = usernameTextField.getText();
            String pass = passwordTextField.getText();
            Account verif = accountService.autentification(user, pass);
            if (verif != null) {

                travelApp(verif);
            } else {
                MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Log In Failed", "An account with this username and password does not exist");
                clearTextFields();
            }
        });*/

    @FXML
    public void handleLogin(ActionEvent event){


        String user = usernameTextField.getText();
        String pass = passwordTextField.getText();
        Account verif = travelServices.autentification(user, pass);
        if (verif != null) {
            ((Node)event.getSource()).getScene().getWindow().hide();
            travelApp(verif);
        } else {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Log In Failed", "An account with this username and password does not exist");
            clearTextFields();
        }
    }


    public void travelApp(Account account) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/travelAppWindow.fxml"));
        try{

            AnchorPane root = loader.load();
            Stage confirmStage = new Stage();
            confirmStage.initModality(Modality.APPLICATION_MODAL);
            confirmStage.setTitle("Travel App");
            MainController mainController=loader.getController();
            travelServices.addTravelObserver(mainController);
            mainController.setService(account,travelServices,confirmStage);
            Scene confirmScene = new Scene(root);
            confirmStage.setScene(confirmScene);
            confirmStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void clearTextFields() {
        this.usernameTextField.clear();
        this.passwordTextField.clear();
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}

