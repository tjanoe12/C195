package Utility;

import Model.Appointment;
import Model.Contact;
import Model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ReportsDB {
    private static Contact newContactSchedule;
    private static Customer newCustomerSchedule;

    /**
     * From the selection of the CB from the Contact Schedules Report
     * */
    public static void sendContactSelection(Contact contactSchedule) {
      /**setting the contact object which will use their Contact ID to query the DB*/
        newContactSchedule = contactSchedule;
    }
    /**Appointments for the sent Contact will be add to this list*/
    public static ObservableList<Appointment> contactSchedule = FXCollections.observableArrayList();


    public static ObservableList<Appointment> getContactSchedule() throws SQLException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        contactSchedule.clear();
        try {
            Connection conn = DBConnection.startConnection();
            /**Selecting all appointments where the Contact ID = the selection from the Combo Box and adding them to the contact schedule list*/
            ResultSet rb = conn.createStatement().executeQuery("SELECT * FROM appointments WHERE Contact_ID=" + newContactSchedule.getContactID());
            while (rb.next()) {
                contactSchedule.add(new Appointment(
                        rb.getInt("Appointment_ID"),
                        rb.getString("Title"),
                        rb.getString("Description"),
                        rb.getString("Type"),
                        rb.getTimestamp("Start").toInstant().atOffset(ZoneOffset.from(ZonedDateTime.now())).toLocalDateTime(),
                        rb.getTimestamp("End").toInstant().atOffset(ZoneOffset.from(ZonedDateTime.now())).toLocalDateTime(),
                        rb.getInt("Customer_ID")));

            }
            return contactSchedule;
        } catch (SQLException e) {
            java.util.logging.Logger.getLogger(e.toString());
        }
        return null;
    }
    /**
     * From the selection of the CB from the Customer Schedules Report*/
    public static void sendCustomerSelection(Customer customerSchedule) {
        //setting the contact object which will use their Contact ID to query the DB
        newCustomerSchedule = customerSchedule;
    }
    /**Appointments for the sent Customer will be add to this list*/
    public static ObservableList<Appointment> customerSchedule = FXCollections.observableArrayList();


    public static ObservableList<Appointment> getCustomerSchedule() throws SQLException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        customerSchedule.clear();
        try {
            Connection conn = DBConnection.startConnection();
            /**Selecting all appointments where the Customer ID = the selection from the Combo Box and adding them to the customer schedule list*/
            ResultSet rb = conn.createStatement().executeQuery("SELECT * FROM appointments WHERE Customer_ID=" + newCustomerSchedule.getCustomerID());
            while (rb.next()) {
                customerSchedule.add(new Appointment(
                        rb.getInt("Appointment_ID"),
                        rb.getString("Title"),
                        rb.getString("Description"),
                        rb.getString("Type"),
                        rb.getTimestamp("Start").toInstant().atOffset(ZoneOffset.from(ZonedDateTime.now())).toLocalDateTime(),
                        rb.getTimestamp("End").toInstant().atOffset(ZoneOffset.from(ZonedDateTime.now())).toLocalDateTime(),
                        rb.getInt("Customer_ID")));

            }
            return customerSchedule;
        } catch (SQLException e) {
            java.util.logging.Logger.getLogger(e.toString());
        }
        return null;
    }
}

