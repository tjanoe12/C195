package Utility;

import Model.Countries;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.logging.Logger;
/**
 * CountryDB is the data exchange for all classes and accessing the MySQL database
 */
public class CountriesDB {
    public static ObservableList<Countries> allCountries = FXCollections.observableArrayList();

    /**
     *Create a list of all appointments
     */
    public static ObservableList<Countries> getAllCountries() throws SQLException {
        allCountries.clear();
        try {
            Connection conn = DBConnection.startConnection();
            ResultSet rb = conn.createStatement().executeQuery("SELECT * FROM countries");
            while (rb.next()) {
                allCountries.add(new Countries(
                        rb.getInt("Country_ID"),
                        rb.getString("Country"),
                        rb.getTimestamp("Create_Date").toInstant().atOffset(ZoneOffset.from(ZonedDateTime.now())).toLocalDateTime(),
                        rb.getString("Created_By"),
                        rb.getTimestamp("Last_Update").toInstant().atOffset(ZoneOffset.from(ZonedDateTime.now())).toLocalDateTime(),
                        rb.getString("Last_Updated_By")));
            }
            return allCountries;
        } catch (SQLException e) {
            Logger.getLogger(e.toString());
        }
        return null;
    }
}
