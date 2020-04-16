package controller;

import entities.Account;
import entities.Flight;
import entities.Ticket;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.TravelObserver;
import service.TravelServices;
import service.TravelServicesProxy;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class MainController implements TravelObserver {

    private static final Logger LOGGER = LogManager.getLogger(TravelServicesProxy.class.getName());
    private TravelServices travelServices;
    private Account account;
    private Stage stage;


    ObservableList<Flight> observableListFlights = FXCollections.observableArrayList();
    ObservableList<Flight> observableListSearched=FXCollections.observableArrayList();


    @FXML
    private TableView<Flight> flightTableView;

    @FXML
    private TableColumn<Flight,Integer> idColumn;

    @FXML
    private TableColumn<Flight,String> destinationColumn;

    @FXML
    private TableColumn<Flight, LocalDate> dateColumn;

    @FXML
    private TableColumn<Flight, LocalTime> timeColumn;

    @FXML
    private TableColumn<Flight,String> airportColumn;

    @FXML
    private TableColumn<Flight,Integer> nrSeatsColumn;

    @FXML
    private TableView<Flight> flightSearchTableView;

    @FXML
    private TableColumn<Flight,Integer> idSColumn;

    @FXML
    private TableColumn<Flight,String> destinationSColumn;

    @FXML
    private TableColumn<Flight, LocalDate> dateSColumn;

    @FXML
    private TableColumn<Flight, LocalTime> timeSColumn;

    @FXML
    private TableColumn<Flight,String> airportSColumn;

    @FXML
    private TableColumn<Flight,Integer> nrSeatsSColumn;

    @FXML
    private Label userLabel;

    @FXML
    private TextField searchDestinationTextField;

    @FXML
    private DatePicker searchDatePicker;

    @FXML
    private TextField clientTextField;

    @FXML
    private TextField touristsTextField;

    @FXML
    private TextField adressTextField;

    @FXML
    private TextField nrSeatsTextField;

    @FXML
    private Label dateLabel;


    public void setService(Account account,TravelServices travelServices, Stage stage){
        this.account=account;
        this.travelServices = travelServices;
        this.stage = stage;
        List<Flight> flights = travelServices.getAllFlights();
        Platform.runLater(() -> {
            observableListFlights.setAll(flights);
        });
        userLabel.setText(account.getUserName());
    }


    public void initialize(){

        idColumn.setCellValueFactory(new PropertyValueFactory<Flight, Integer>("id"));
        destinationColumn.setCellValueFactory(new PropertyValueFactory<Flight,String>("destination"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<Flight,LocalDate>("date"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<Flight,LocalTime>("time"));
        airportColumn.setCellValueFactory(new PropertyValueFactory<Flight,String>("airportName"));
        nrSeatsColumn.setCellValueFactory(new PropertyValueFactory<Flight,Integer>("availableSeats"));
        flightTableView.setItems(observableListFlights);
        flightTableView.setRowFactory(row -> getTableRow());

        idSColumn.setCellValueFactory(new PropertyValueFactory<Flight, Integer>("id"));
        destinationSColumn.setCellValueFactory(new PropertyValueFactory<Flight,String>("destination"));
        dateSColumn.setCellValueFactory(new PropertyValueFactory<Flight,LocalDate>("date"));
        timeSColumn.setCellValueFactory(new PropertyValueFactory<Flight,LocalTime>("time"));
        airportSColumn.setCellValueFactory(new PropertyValueFactory<Flight,String>("airportName"));
        nrSeatsSColumn.setCellValueFactory(new PropertyValueFactory<Flight,Integer>("availableSeats"));
        flightSearchTableView.setItems(observableListSearched);
        flightSearchTableView.setRowFactory(row -> getTableRow());

    }

    private TableRow<Flight> getTableRow() {
        return new TableRow<Flight>() {
            @Override
            public void updateItem(Flight item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setStyle("");
                } else {
                    if (item.getAvailableSeats() == 0) {
//                        for (int i = 0; i < getChildren().size(); i++) {
//                            ((Labeled) getChildren().get(i)).setTextFill(Color.RED);
//                        }
                        setStyle("-fx-background-color: #F07470");
                    }
                }
            }
        };
    }


    public void handleLogout(ActionEvent event)  {
        try {
            ((Node)event.getSource()).getScene().getWindow().hide();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/loginWindow.fxml"));
            AnchorPane root = loader.load();
            LoginController ctrl=loader.getController();
            ctrl.setService(travelServices, stage);
            Stage confirmStage = new Stage();
            confirmStage.initModality(Modality.APPLICATION_MODAL);
            confirmStage.setTitle("Log in");
            Scene confirmScene = new Scene(root);
            confirmStage.setScene(confirmScene);
            confirmStage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }




    public void handleFindFlights(){

        String destination=this.searchDestinationTextField.getText();
        LocalDate date=this.searchDatePicker.getValue();
        dateLabel.setText(searchDatePicker.getValue().toString());

        if (destination.equals("")){
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Error","You need to type in a DESTINATION!");
            return;
        }

        if (date==null){
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Error","You need to select a DATE!");
            return;
        }



        List<Flight> trips = travelServices.getFlightsbyDestAndDate(destination, date);
        Platform.runLater(() -> {
            observableListSearched.setAll(trips);
        });


    }

    public void handleAddTicket()
    {
        Flight f = flightSearchTableView.getSelectionModel().getSelectedItem();
        if (f == null) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Error","You need to select a flight in order to buy a ticket!");
            return;
        }

        String clientName = clientTextField.getText();
        if (clientName.equals("")){
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Error","You need to type in a client's name.");
            return;
        }

        String tourists = touristsTextField.getText();

        if (tourists.equals("")){
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Error","You need to type in the tourists' names!");
            return;
        }

        String adress = adressTextField.getText();

        if (adress.equals("")){
            MessageAlert.showMessage(null,Alert.AlertType.ERROR,"Error","You need to type in the client's adress!");
            return;
        }

        try {
            int nrSeats = Integer.parseInt(nrSeatsTextField.getText());
            String[] touristsList = tourists.split(";");

            if (touristsList.length != nrSeats) {
                MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "The number of seats doesn't match the number of tourists you introduced!");
                return;
            }


            this.travelServices.addTicket(clientName,tourists,adress,nrSeats,f, account);
            clearFields();
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Added", "The ticket was added.");


        }
        catch (NumberFormatException e) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Error","Invalid number of seats!");
            return;
        }


    }


    public void clearFields(){
        this.clientTextField.clear();
        this.touristsTextField.clear();
        this.adressTextField.clear();
        this.nrSeatsTextField.clear();
    }

    @Override
    public void ticketInserted(Ticket ticket) {
        Platform.runLater(() -> {
            LOGGER.info("booking inserted " + ticket + " updating table views");
            updateTableRow(ticket, observableListFlights);
            updateTableRow(ticket, observableListSearched);
        });
    }

    private void updateTableRow(Ticket ticket, ObservableList<Flight> flights) {
        for (int i = 0; i < flights.size(); i++) {
            Flight f = flights.get(i);
            if (f.getId().equals(ticket.getFlightId())) {
                f.setAvailableSeats(f.getAvailableSeats() - ticket.getNrSeats());
                flights.set(i, f);
                break;
            }
        }
    }




}
