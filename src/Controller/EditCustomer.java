package Controller;
/**
 * Allows the user to edit a customer utilizing the CustomerDB to access and use the MySQL DB
 */

import Model.Countries;
import Model.Customer;
import Model.FirstLevelDivision;
import Utility.CountriesDB;
import Utility.CustomerDB;
import Utility.FirstLevelDivisionDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class EditCustomer implements Initializable {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    Long offsetToUTC = Long.valueOf((ZonedDateTime.now().getOffset()).getTotalSeconds());

    private Customer newModifyCustomer;

    @FXML
    private Button exit;

    @FXML
    private Button addCustomer;

    @FXML
    private TextField custIDTxt;

    @FXML
    private TextField custNameTxt;

    @FXML
    private TextField custAddressTxt;

    @FXML
    private TextField custPostalTxt;

    @FXML
    private TextField custPhoneTxt;

    @FXML
    private TextField lastUpdatedByTF;

    @FXML
    private TextField lastUpdateTF;

    @FXML
    private TextField createdByTF;

    @FXML
    private TextField createDateTF;

    @FXML
    private ComboBox<Countries> cbCountry;

    @FXML
    private ComboBox<FirstLevelDivision> cbDivID;

    public EditCustomer() throws SQLException {
    }

    /**
     * Returns updated fields to the db
     * @see CustomerDB#editCustomer(Integer, String, String, String, String, Timestamp, String, Timestamp, String, Integer)
     * @param event
     * @return
     * @throws SQLException
     * @throws IOException
     */
    @FXML
    boolean editCustomer(ActionEvent event) throws SQLException, IOException {
        try {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/View/CustomerMain.fxml"));
        Parent parent = loader.load();

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Parent scene = loader.getRoot();
        stage.setScene(new Scene(scene));
        stage.show();

        return CustomerDB.editCustomer(
                Integer.valueOf(custIDTxt.getText()),
                custNameTxt.getText(),
                custAddressTxt.getText(),
                custPostalTxt.getText(),
                custPhoneTxt.getText(),
                Timestamp.valueOf(LocalDateTime.parse(createDateTF.getText(), formatter).minus(Duration.ofSeconds(offsetToUTC))),
                createdByTF.getText(),
                Timestamp.valueOf(LocalDateTime.parse(lastUpdateTF.getText(), formatter).minus(Duration.ofSeconds(offsetToUTC))),
                lastUpdatedByTF.getText(),
                Integer.valueOf(String.valueOf(cbDivID.getSelectionModel().getSelectedItem().getDivisionID())));
        }
      catch (DateTimeParseException e) {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Missing selection");
          alert.setContentText("Please ensure all date and time fields are formatted YYYY-MM-DD HH:MM prior to adding an appointment");
          alert.showAndWait();
          return false;
      }

        }

    @FXML
    private void addCustomer(ActionEvent event)
    {

    }

    /**
     * Sets the object selected from Customer main tableview
     * Utilizes
     * @see CustomerMain#sceneEditCustomer(ActionEvent)
     * @param modifyCustomer
     */
    @FXML
    public void sendCustomer(Customer modifyCustomer)
    {
        newModifyCustomer = modifyCustomer;
        custIDTxt.setText(String.valueOf(newModifyCustomer.getCustomerID()));
        custNameTxt.setText(newModifyCustomer.getCustomerName());
        custAddressTxt.setText(newModifyCustomer.getAddress());
        custPostalTxt.setText(newModifyCustomer.getPostal());
        custPhoneTxt.setText(String.valueOf(newModifyCustomer.getPhone()));
        lastUpdatedByTF.setText(newModifyCustomer.getLastUpdatedBy());
        lastUpdateTF.setText(String.valueOf(newModifyCustomer.getLastUpdate().format(formatter)));
        createdByTF.setText(newModifyCustomer.getCreatedBy());
        createDateTF.setText(String.valueOf(newModifyCustomer.getCreateDate().format(formatter)));
        int comboBoxPreset = newModifyCustomer.getDivisionID();
        FirstLevelDivision fld = new FirstLevelDivision(comboBoxPreset);
        cbDivID.setValue(fld);
/**
 * Sets the combobox of the division name based on country selected
 */
        if (fld.getDivisionID() <= 54)
        {
            String countryName = "U.S";
            Countries c = new Countries(countryName);
            cbCountry.setValue(c);
        }
        else if (fld.getDivisionID() >54 && fld.getDivisionID() <= 72)
        {
            String countryName = "UK";
            Countries c = new Countries(countryName);
            cbCountry.setValue(c);
        }
        else if (fld.getDivisionID() > 72)
        {
            String countryName = "Canada";
            Countries c = new Countries(countryName);
            cbCountry.setValue(c);
        }

        try {
            cbCountry.setItems(CountriesDB.getAllCountries());
            for (Countries countries : CountriesDB.allCountries) {
                System.out.println(countries.getCountry());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            cbDivID.setItems(FirstLevelDivisionDB.getAllFirstLevelDivisions());
            for (FirstLevelDivision firstLevelDivision : FirstLevelDivisionDB.allFirstLevelDivisions) {
                System.out.println(firstLevelDivision.getDivision());
            }
            cbDivID.setValue(fld);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    ObservableList<FirstLevelDivision> firstLevelDivisionObservableList = FirstLevelDivisionDB.getAllFirstLevelDivisions();
    ObservableList<FirstLevelDivision> usFirstLevelDivisionObservableList = FXCollections.observableArrayList();
    ObservableList<FirstLevelDivision> canadaFirstLevelDivisionObservableList = FXCollections.observableArrayList();
    ObservableList<FirstLevelDivision> ukFirstLevelDivisionObservableList = FXCollections.observableArrayList();

    /**
    Create lambda filtered lists for each country based on division id and country id utilizing a predicate f, and sorting through the objects
     https://www.javacodegeeks.com/2018/07/filter-method-java-8.html used for filter set up
    */
    @FXML
    private void SetDivisionID(ActionEvent event) throws IOException, SQLException {
        if (cbCountry.getSelectionModel().isEmpty()) {
            System.out.println(cbCountry.getSelectionModel().toString());
            return;
        }
        /**
         * Using a stream filter to set the selectable divisions based on country selected from combobox
         */
        //US Filter
        else if (cbCountry.getSelectionModel().getSelectedItem().getCountry().equals("U.S")) {
            /**
             * lambda expression of predicate using f as an object of First Level Division
             */
            var usResult = firstLevelDivisionObservableList.stream().filter(f -> f.getDivisionID() < 54).collect(Collectors.toList());
            cbDivID.setItems(usFirstLevelDivisionObservableList = FXCollections.observableList(usResult));
        }
        //Canada Filter
        else if (cbCountry.getSelectionModel().getSelectedItem().getCountry().equals("Canada")) {
            var canadaResult = firstLevelDivisionObservableList.stream().filter(f -> (f.getDivisionID() > 54) && (f.getDivisionID() < 101)).collect(Collectors.toList());
            cbDivID.setItems(canadaFirstLevelDivisionObservableList = FXCollections.observableList(canadaResult));
        }
        //UK Filter
        else if (cbCountry.getSelectionModel().getSelectedItem().getCountry().equals("UK")) {
            var ukResult = firstLevelDivisionObservableList.stream().filter(f -> f.getDivisionID() >= 101).collect(Collectors.toList());
            cbDivID.setItems(ukFirstLevelDivisionObservableList = FXCollections.observableList(ukResult));
        }

    }


    @FXML
    void exitMainMenu(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Object scene = FXMLLoader.load(getClass().getResource("/View/CustomerMain.fxml"));
        stage.setScene(new Scene((Parent) scene));
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
