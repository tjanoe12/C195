package Controller;
/**
 * Main Customer class where add, edit, and delete are available. Shows a tableview with all Customers
 */

import Model.Customer;
import Utility.CustomerDB;
import Utility.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class CustomerMain implements Initializable {

    @FXML
    private TableView<Customer> custTable;

    @FXML
    private TableColumn<Customer, Integer> custCustomerId;

    @FXML
    private TableColumn<Customer, String> custName;

    @FXML
    private TableColumn<Customer, String> custAddress;

    @FXML
    private TableColumn<Customer, String> custPostal;

    @FXML
    private TableColumn<Customer, String> custPhone;

    @FXML
    private TableColumn<Customer, LocalDateTime> custCreateDate;

    @FXML
    private TableColumn<Customer, String> custCreatedBy;

    @FXML
    private TableColumn<Customer, Timestamp> custLastUpdate;

    @FXML
    private TableColumn<Customer, String> custLastUpdatedBy;

    @FXML
    private TableColumn<Customer, Integer> custDivisionID;

    @FXML
    private Button addCustomer;

    @FXML
    private Button deleteCustomer;

    @FXML
    private Button editCustomer;

    @FXML
    private Button mainButton;

    @FXML
    private Button exitButton;

    @FXML
    private ComboBox<Customer> cbCustomerTable;

    @FXML
    void exitApp(ActionEvent event) {
        Button sourceButton = (Button) event.getSource();
        exitButton.setText(sourceButton.getText());
        DBConnection.closeConnection();
        System.exit(0);
    }

    /**
     * Access to add a new customer to the DB
     * @see Utility.CustomerDB#addCustomer(Integer, String, String, String, String, LocalDateTime, String, LocalDateTime, String, Integer)
     * @param event
     * @throws IOException
     */
    @FXML
    void sceneAddCustomer(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/View/AddCustomer.fxml"));
        loader.load();

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Parent scene = loader.getRoot();
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Delete a customer interfaces with
     * @see Utility.CustomerDB#deleteCustomer(int)
     * @param event
     * @throws SQLException
     * @throws IOException
     */
    @FXML
    void sceneDeleteCustomer(ActionEvent event) throws SQLException, IOException {
        try {
            Customer selectedItem = custTable.getSelectionModel().getSelectedItem();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Warning");
            alert.setHeaderText("All associated appointments for " + selectedItem.getCustomerName() + " will be deleted");
            alert.setContentText("Are you sure you want to delete the customer?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                CustomerDB.deleteCustomer(selectedItem.getCustomerID());
                System.out.println("Deletion Successful!");
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/View/CustomerMain.fxml"));
                Parent parent = loader.load();

                Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                Parent scene = loader.getRoot();
                stage.setScene(new Scene(scene));
                stage.show();

            }
        } catch (IOException e) {
            String s = "a customer";
        }

    }

    @FXML
    void sceneBackToMain(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/View/MainMenu.fxml"));
        loader.load();

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Parent scene = loader.getRoot();
        stage.setScene(new Scene(scene));
        stage.show();
    }


    /**
     * Populates an edit scene upon user selection of an item from the table view, if nothing is selected an alert pops requesting a selection
     * @see Controller.EditCustomer
     * @param event
     * @throws IOException
     */
    @FXML
    void sceneEditCustomer(ActionEvent event) throws IOException {
        try {
            Customer modifyCustomer = custTable.getSelectionModel().getSelectedItem();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/View/EditCustomer.fxml"));
            Parent parent = loader.load();
            Scene modifyCustomerScene = new Scene(parent);
            EditCustomer controller = loader.getController();
            controller.sendCustomer(modifyCustomer);

            Stage window = (Stage) ((Button) event.getSource()).getScene().getWindow();
            window.setScene(modifyCustomerScene);
            window.setResizable(false);
            window.show();
        }
        catch (NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Select a Customer");
            alert.setHeaderText("Please select a customer to edit");
            alert.setContentText("No customer selected!");
        }

    }

    @FXML
    void onComboBoxSelect(ActionEvent event) {

    }
    public CustomerMain() {
        //Set New Customer ID Auto-Increment
        try {
            Statement statement = DBConnection.startConnection().createStatement();
            statement.executeUpdate("ALTER TABLE customers AUTO_INCREMENT");
        } catch (SQLException ce) {
            Logger.getLogger(ce.toString());
        }
    }

    /**
     * Sets the values of the tableview from the MySQL DB
     * @see CustomerDB#getAllCustomers()
     * try statement runs a for each lambda to get all objects
     * @param url
     * @param resourceBundle
     */
    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Customer Table
        custCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        custName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        custAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        custPostal.setCellValueFactory(new PropertyValueFactory<>("postal"));
        custPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        custCreateDate.setCellValueFactory(new PropertyValueFactory<>("createDate"));
        custCreatedBy.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
        custLastUpdate.setCellValueFactory(new PropertyValueFactory<>("lastUpdate"));
        custLastUpdatedBy.setCellValueFactory(new PropertyValueFactory<>("lastUpdatedBy"));
        custDivisionID.setCellValueFactory(new PropertyValueFactory<>("divisionID"));

        try {
           custTable.setItems(CustomerDB.getAllCustomers());
           for (Customer customer : CustomerDB.allCustomers)
           {
               System.out.println(customer.getCustomerName());
           }
            }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
