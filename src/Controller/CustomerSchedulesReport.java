package Controller;
/**
 * Displays the total appointments by month and by type
 */
import Model.Appointment;
import Model.Customer;
import Utility.CustomerDB;
import Utility.DBConnection;
import Utility.ReportsDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class CustomerSchedulesReport implements Initializable {

    @FXML
    private ComboBox<Customer> customerCB;

    @FXML
    private TableView<Appointment> customerAppointmentTbl;

    @FXML
    private TableColumn<Appointment, Integer> appointmentID;

    @FXML
    private TableColumn<Appointment, String> aptTitle;

    @FXML
    private TableColumn<Appointment, String> aptType;

    @FXML
    private TableColumn<Appointment, String> aptDescription;

    @FXML
    private TableColumn<Appointment, LocalDateTime> aptStart;

    @FXML
    private TableColumn<Appointment, LocalDateTime> aptEnd;

    @FXML
    private TableColumn<Appointment, Integer> aptCustID;

    @FXML
    private Button exitBtn;

    @FXML
    private Button mainBtn;

    @FXML
    void backToMain(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/View/MainMenu.fxml"));
        loader.load();

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Parent scene = loader.getRoot();
        stage.setScene(new Scene(scene));
        stage.show();
    }

    @FXML
    void displayCustomerSchedule(ActionEvent event) {
        try {
            /**selecting the customer object to send to query the DB*/
            Customer customerSchedule = customerCB.getSelectionModel().getSelectedItem();
            ReportsDB.sendCustomerSelection(customerSchedule);
            /**Getting the appointments from the DB and populating the tableview*/
            customerAppointmentTbl.setItems(ReportsDB.getCustomerSchedule());
            for (Appointment appointment : ReportsDB.customerSchedule) {
                System.out.println(appointment.getStart());
            }
            appointmentID.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
            aptTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
            aptType.setCellValueFactory(new PropertyValueFactory<>("type"));
            aptDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
            aptStart.setCellValueFactory(new PropertyValueFactory<>("start"));
            aptEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
            aptCustID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void exitApp(ActionEvent event) {
        Button sourceButton = (Button) event.getSource();
        exitBtn.setText(sourceButton.getText());
        DBConnection.closeConnection();
        System.exit(0);
    }

    ObservableList<Customer> customerList = FXCollections.observableArrayList();

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            customerCB.setItems(CustomerDB.getAllCustomers());
            for (Customer customer : CustomerDB.allCustomers) {
                System.out.println(customer.getCustomerID());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
