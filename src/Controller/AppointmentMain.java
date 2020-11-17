package Controller;
/**
 * Main Appointment class where add, edit, and delete are available. Shows a tableview with all Appointments
 */

import Model.Appointment;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static Utility.AppointmentDB.deleteAppointment;

public class AppointmentMain implements Initializable {

    /**
     * @param allAptsRb shows all appointments
     */
    @FXML
    private RadioButton allAptsRB;

    @FXML
    private ToggleGroup aptTableTGLGRP;
    /**
     * @param weeklyRB shows weekly appointments
     */
    @FXML
    private RadioButton weeklyRB;

    /**
     * @param monthlyRB shows monthly appointments
     */
    @FXML
    private RadioButton monthlyRB;


    @FXML
    private TableView<Appointment> aptTable;

    @FXML
    private TableColumn<Appointment, Integer> aptAppointmentID;

    @FXML
    private TableColumn<Appointment, String> aptTitle;

    @FXML
    private TableColumn<Appointment, String> aptDescription;

    @FXML
    private TableColumn<Appointment, String> aptLocation;

    @FXML
    private TableColumn<Appointment, String> aptType;

    @FXML
    private TableColumn<Appointment, LocalDateTime> aptStart;

    @FXML
    private TableColumn<Appointment, LocalDateTime> aptEnd;

    @FXML
    private TableColumn<Appointment, LocalDateTime> aptCreateDate;

    @FXML
    private TableColumn<Appointment, String> aptCreatedBy;

    @FXML
    private TableColumn<Appointment, LocalDateTime> aptLastUpdate;

    @FXML
    private TableColumn<Appointment, String> aptLastUpdatedBy;

    @FXML
    private TableColumn<Appointment, Integer> aptCID;

    @FXML
    private TableColumn<Appointment, Integer> aptUID;

    @FXML
    private TableColumn<Appointment, Integer> aptContID;

    @FXML
    private Button addAppointment;

    @FXML
    private Button editAppointment;

    @FXML
    private Button deleteAppointment;

    @FXML
    private Button menuButton;

    @FXML
    private Button exitButton;

    ObservableList<Appointment> aptList = AppointmentDB.getAllAppointments();
    ObservableList<Appointment> weeklyAppointmentList = FXCollections.observableArrayList();
    ObservableList<Appointment> monthlyAppointmentList = FXCollections.observableArrayList();

    public AppointmentMain() throws SQLException {
    }

    /**
     * Utilizing a for each lambda loop to reduce amount of code and improve readability
     * @param event
     * @throws SQLException
     */
    @FXML
    void RBallAppointmentsOA(ActionEvent event) throws SQLException {

        try {
            aptTable.setItems(AppointmentDB.getAllAppointments());
            /**For each lambda to get all appointments, less code then normal for i method*/
            for (Appointment appointment : AppointmentDB.allAppointments) {
                System.out.println(appointment.getStart());
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            }
        }


    @FXML
    void RBweeklyAppointmentsOA(ActionEvent event) throws SQLException {
        /**Date variables for comparative operations of the start date from the Appointment list and the system date pulled from User's system.*/
        LocalDate today = LocalDate.from(ZonedDateTime.now());
        LocalDate oneWeekFromToday = LocalDate.from(ZonedDateTime.now()).plusWeeks(1);


        if ((this.aptTableTGLGRP.getSelectedToggle().equals(this.weeklyRB))) {
/**Predicate looks at today's date or after AND before one week from today's system date*/
            Predicate<Appointment> weeklyView = appointment -> (appointment.getStart().toLocalDate().equals(today))
                    || appointment.getStart().toLocalDate().isAfter(today)
                    && appointment.getStart().toLocalDate().isBefore((oneWeekFromToday));
            System.out.println(today);

            var weeklyResult = aptList.stream().filter(weeklyView).collect(Collectors.toList());
            /**Cast the list to the Appointment Observable List*/
            aptTable.setItems(weeklyAppointmentList = FXCollections.observableList(weeklyResult));
        }
    }

    /**
     * Filters based on monthly radio button selection to show only appointment
     *
     * @param event
     */
    @FXML
    void RBmonthlyAppointmentsOA(ActionEvent event) {

        //Date variables for comparative operations of the start date from the Appointment list and the system date pulled from User's system.
        LocalDate today = LocalDate.from(ZonedDateTime.now());
        LocalDate oneMonthFromToday = LocalDate.from(ZonedDateTime.now()).plusMonths(1);
        if ((this.aptTableTGLGRP.getSelectedToggle().equals(this.monthlyRB))) {
            Predicate<Appointment> monthlyView = appointment -> (appointment.getStart().toLocalDate().equals(today))
                    || appointment.getStart().toLocalDate().isAfter((today))
                    && appointment.getStart().toLocalDate().isBefore((oneMonthFromToday));
            System.out.println(today);

            var monthList = aptList.stream().filter(monthlyView).collect(Collectors.toList());
            //Cast the list to the Appointment Observable List
            aptTable.setItems(monthlyAppointmentList = FXCollections.observableList(monthList));
        }
    }

    @FXML
    void exitApp(ActionEvent event) {
        Button sourceButton = (Button) event.getSource();
        exitButton.setText(sourceButton.getText());
        DBConnection.closeConnection();
        System.exit(0);
    }


    @FXML
    void sceneAddAppointment(ActionEvent event) throws IOException {
        /**
         * @see AppointmentDB#addAppointment(Integer, String, String, String, String, LocalDateTime, LocalDateTime, LocalDateTime, String, LocalDateTime, String, Integer, Integer, Integer)
         */
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/View/AddAppointment.fxml"));
        loader.load();

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Parent scene = loader.getRoot();
        stage.setScene(new Scene(scene));
        stage.show();
    }

    @FXML
    void sceneDeleteAppointment(ActionEvent event) {
        try {
            /**
             * @see AppointmentDB#deleteAppointment(int)
             */
            Appointment selectedItem = aptTable.getSelectionModel().getSelectedItem();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Warning");
            alert.setHeaderText("Delete appointment type: " + selectedItem.getType() + " ID Number: " + selectedItem.getAppointmentID() + " ?");
            alert.setContentText("Are you sure you want to delete the appointment?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                deleteAppointment(selectedItem.getAppointmentID());
                System.out.println("Appointment: " + selectedItem.getAppointmentID() + " Successful!");
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/View/AppointmentMain.fxml"));
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
    void sceneEditAppointment(ActionEvent event) throws IOException {
        try {
            /**
             * @see AppointmentDB#editAppointment(Integer, String, String, String, String, LocalDateTime, LocalDateTime, LocalDateTime, String, LocalDateTime, String, Integer, Integer, Integer)
             */
            Appointment modifyAppointment = aptTable.getSelectionModel().getSelectedItem();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/View/EditAppointment.fxml"));
            Parent parent = loader.load();
            Scene modifyCustomerScene = new Scene(parent);
            /**Sending the selected object from the table to a method on the Edit Appointment Scene*/
            EditAppointment controller = loader.getController();
            controller.sendAppointment(modifyAppointment);

            Stage window = (Stage) ((Button) event.getSource()).getScene().getWindow();
            window.setScene(modifyCustomerScene);
            window.setResizable(false);
            window.show();
        }
        catch (NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Missing selection");
            alert.setContentText("Please select an appointment you would like to edit.");
            alert.showAndWait();
        }
    }

    @FXML
    void sceneMainMenu(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/View/MainMenu.fxml"));
        loader.load();

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Parent scene = loader.getRoot();
        stage.setScene(new Scene(scene));
        stage.show();
    }

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Appointments Table
        aptAppointmentID.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        aptTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        aptLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        aptDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        aptType.setCellValueFactory(new PropertyValueFactory<>("type"));
        aptStart.setCellValueFactory(new PropertyValueFactory<>("start"));
        aptEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
        aptCreateDate.setCellValueFactory(new PropertyValueFactory<>("createDate"));
        aptCreatedBy.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
        aptLastUpdate.setCellValueFactory(new PropertyValueFactory<>("lastUpdate"));
        aptLastUpdatedBy.setCellValueFactory(new PropertyValueFactory<>("lastUpdatedBy"));
        aptCID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        aptUID.setCellValueFactory(new PropertyValueFactory<>("userID"));
        aptContID.setCellValueFactory(new PropertyValueFactory<>("contactID"));
        try {
            aptTable.setItems(AppointmentDB.getAllAppointments());
            /**For each lambda to get all appointments, less code then normal for i method*/
            for (Appointment appointment : AppointmentDB.allAppointments) {
                System.out.println(appointment.getStart());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
