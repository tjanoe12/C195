package Controller;
/**
 * Allows the user to edit an appointment using the AppointmentDB class to modify the DB
 */

import Model.Appointment;
import Model.Contact;
import Utility.AppointmentDB;
import Utility.DBConnection;
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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.logging.Logger;

public class EditAppointment implements Initializable {
    private Appointment newModifyAppointment;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    Long offsetToUTC = Long.valueOf((ZonedDateTime.now().getOffset()).getTotalSeconds());

    @FXML
    private TextField aptIDtxt;

    @FXML
    private TextField aptTitleTxt;

    @FXML
    private TextField aptDescrTxt;

    @FXML
    private TextField aptLocTxt;

    @FXML
    private TextField aptTypeTxt;

    @FXML
    private TextField aptCreateByTxt;

    @FXML
    private TextField aptLstUpdByTxt;

    @FXML
    private TextField aptCustIDTxt;

    @FXML
    private TextField aptUIDTxt;

    @FXML
    private TextField aptContIDTxt;

    @FXML
    private Button EditAppointmentBtn;

    @FXML
    private Button ExitBtn;

    @FXML
    private TextField aptStartTxt;

    @FXML
    private TextField aptEndTxt;

    @FXML
    private TextField aptCreateDateTxt;

    @FXML
    private TextField aptLastUpdateTxt;

    @FXML
    private ComboBox<Contact> contactName;
    ObservableList<Contact> contactList = FXCollections.observableArrayList();

    public EditAppointment() throws SQLException {
        try {
            Connection conn = DBConnection.startConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM contacts");
            while (rs.next()) {
                /**Create Country Objects vice Strings for Country selection
                //columnLabel corresponds to Column! not the attribute of the object*/
                contactList.add(new Contact(rs.getInt("Contact_ID"),rs.getString("Contact_Name"),rs.getString("Email")));

            }
        } catch (SQLException ce) {
            Logger.getLogger(ce.toString());
        }
    }

    @FXML
    private void SetContactID (ActionEvent event) throws IOException {

    }

    /**
     * @see AppointmentMain#sceneEditAppointment(ActionEvent)
     * Sets the fields based on selection from Appointment Main Table View
     * @param modifyAppointment
     */
    @FXML
    public void sendAppointment(Appointment modifyAppointment)
    {
        newModifyAppointment = modifyAppointment;
        aptIDtxt.setText(String.valueOf(newModifyAppointment.getAppointmentID()));
        aptTitleTxt.setText(newModifyAppointment.getTitle());
        aptDescrTxt.setText(newModifyAppointment.getDescription());
        aptLocTxt.setText(newModifyAppointment.getLocation());
        aptTypeTxt.setText(newModifyAppointment.getType());
        aptStartTxt.setText(String.valueOf(newModifyAppointment.getStart().format(formatter)));
        aptEndTxt.setText(String.valueOf(newModifyAppointment.getEnd().format(formatter)));
        aptLstUpdByTxt.setText(newModifyAppointment.getLastUpdatedBy());
        aptLastUpdateTxt.setText(String.valueOf(newModifyAppointment.getLastUpdate().format(formatter)));
        aptCreateByTxt.setText(newModifyAppointment.getCreatedBy());
        aptCreateDateTxt.setText(String.valueOf(newModifyAppointment.getCreateDate().format(formatter)));
        aptCustIDTxt.setText(String.valueOf(newModifyAppointment.getCustomerID()));
        aptUIDTxt.setText(String.valueOf(newModifyAppointment.getUserID()));
        aptContIDTxt.setText(String.valueOf(newModifyAppointment.getContactID()));
        int comboBoxPreset = newModifyAppointment.getContactID();
        Contact c = new Contact(comboBoxPreset);
        contactName.setValue(c);
    }

    @FXML
    void ExitToMain(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Object scene = FXMLLoader.load(getClass().getResource("/View/AppointmentMain.fxml"));
        stage.setScene(new Scene((Parent) scene));
        stage.show();
    }

    /**
     * Gets contact object and sets Id text field based on name selection in combobox
     * @param event
     */
    @FXML
    void OAFillContID(ActionEvent event) {
        if (contactName.getSelectionModel().isEmpty()) {
            return;
        }
        else {
            Contact c = contactName.getSelectionModel().getSelectedItem();
            aptContIDTxt.setText(String.valueOf(c.getContactID()));
        }
    }

    /**
     * @see AppointmentDB#editAppointment(Integer, String, String, String, String, LocalDateTime, LocalDateTime, LocalDateTime, String, LocalDateTime, String, Integer, Integer, Integer)
     * Sends the edited appointment to the DB
     * @param event
     * @return
     * @throws SQLException
     * @throws IOException
     */
    @FXML
    boolean OnActionEditAppointment(ActionEvent event) throws SQLException, IOException{
        TimeZone est = TimeZone.getTimeZone("America/New_York");
        Long offsetToEST = Long.valueOf(est.getOffset(new Date().getTime()) /1000 /60);
        LocalDateTime startTime = LocalDateTime.parse(aptStartTxt.getText(), formatter).minus(Duration.ofSeconds(offsetToUTC));
        /**
         * Set the start time to EST
         */
        startTime = startTime.plus(Duration.ofMinutes(offsetToEST));
        /**
         *Get the time entered (user local) and set it to utc
         */
        LocalDateTime endTime = LocalDateTime.parse(aptEndTxt.getText(), formatter).minus(Duration.ofSeconds(offsetToUTC));
        /**
         *Set the end time to EST
         */
        endTime = endTime.plus(Duration.ofMinutes(offsetToEST));

        /**
         * Compare startTime and endTime between business hours of 8-22
         */

        LocalTime businessHoursStart = LocalTime.of(8, 00);
        LocalTime businessHoursEnd = LocalTime.of(22, 00);

        /**
         * Use to check if date time falls between other scheduled appointments
         */
        LocalDateTime startDateTime = LocalDateTime.parse(aptStartTxt.getText(), formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(aptEndTxt.getText(), formatter);
       try {
           /**
            * verifies all fields are entered otherwise gives an alert to enter fields
            */
           if (aptTitleTxt.getText().isEmpty() || aptDescrTxt.getText().isEmpty() || aptLocTxt.getText().isEmpty() || aptTypeTxt.getText().isEmpty() || aptStartTxt.getText().isEmpty() || aptEndTxt.getText().isEmpty() || aptCreateDateTxt.getText().isEmpty() || aptCreateByTxt.getText().isEmpty() || aptLastUpdateTxt.getText().isEmpty() || aptLstUpdByTxt.getText().isEmpty() || aptCustIDTxt.getText().isEmpty() || aptCustIDTxt.getText().isEmpty() || aptContIDTxt.getText().isEmpty()) {
               Alert alert = new Alert(Alert.AlertType.ERROR);
               alert.setTitle("Missing Entries");
               alert.setContentText("Please ensure all fields are entered");
               alert.showAndWait();
           }


           /**
            * Check for overlapping appointment times
            */

           for (Appointment appointment : AppointmentDB.allAppointments) {
               if((startDateTime.isEqual(appointment.getStart()) || startDateTime.isAfter(appointment.getStart()) && startDateTime.isBefore(appointment.getEnd()))) {
                   Alert alert = new Alert(Alert.AlertType.ERROR);
                   alert.setTitle("CONFLICT");
                   alert.setContentText("Please enter a time for the start and end time of the appointment that is not already taken");
                   alert.showAndWait();
                   return false;
               }
           }

           /**
            * Check if time of start and end are within the business hours
            */

           if (startTime.toLocalTime().isBefore(businessHoursStart) || endTime.toLocalTime().isAfter(businessHoursEnd)) {
               Alert alert = new Alert(Alert.AlertType.ERROR);
               alert.setTitle("TOO EARLY!");
               alert.setContentText("Please enter a time after business opening hour of 0800 EST and before business closing hours of 1000 EST");
               alert.showAndWait();

           }

           /**
            * If fields are not blank sends Appointment object to DB
            * @see AppointmentDB#editAppointment(Integer, String, String, String, String, LocalDateTime, LocalDateTime, LocalDateTime, String, LocalDateTime, String, Integer, Integer, Integer)
            *
            */
           else if (!aptTitleTxt.equals("") && !aptTypeTxt.equals("") && !aptDescrTxt.equals("") && !aptLocTxt.equals("")){
               FXMLLoader loader = new FXMLLoader();
               loader.setLocation(getClass().getResource("/View/AppointmentMain.fxml"));
               Parent parent = loader.load();

               Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
               Parent scene = loader.getRoot();
               stage.setScene(new Scene(scene));
               stage.show();

               return AppointmentDB.editAppointment(Integer.valueOf(
                       aptIDtxt.getText()),
                       aptTitleTxt.getText(),
                       aptDescrTxt.getText(),
                       aptLocTxt.getText(),
                       aptTypeTxt.getText(),
                       LocalDateTime.parse(aptStartTxt.getText(), formatter).minus(Duration.ofSeconds(offsetToUTC)),
                       LocalDateTime.parse(aptEndTxt.getText(), formatter).minus(Duration.ofSeconds(offsetToUTC)),
                       LocalDateTime.parse(aptCreateDateTxt.getText(), formatter).minus(Duration.ofSeconds(offsetToUTC)),
                       aptCreateByTxt.getText(),
                       LocalDateTime.parse(aptLastUpdateTxt.getText(), formatter).minus(Duration.ofSeconds(offsetToUTC)),
                       aptLstUpdByTxt.getText(),
                       Integer.valueOf(aptCustIDTxt.getText()),
                       Integer.valueOf(aptUIDTxt.getText()),
                       Integer.valueOf(aptContIDTxt.getText()));
           }
       }
       /**
        * @exception DateTimeParseException e if date time fields are not formatted correctly this is caught to alert the user to modify them correctly
        */
        catch (DateTimeParseException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Missing selection");
            alert.setContentText("Please ensure all date and time fields are formatted YYYY-MM-DD HH:MM prior to adding an appointment");
            alert.showAndWait();
            return false;
        }
       return false;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        contactName.setItems(contactList);

    }
}

