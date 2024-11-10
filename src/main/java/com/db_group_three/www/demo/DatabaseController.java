package com.db_group_three.www.demo;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseController {

    @FXML
    private Pane dbPane;
    // Fields for Vehicle Trends tab
    @FXML
    private TextField makeField;
    @FXML
    private TextField modelField;
    @FXML
    private Button vSearchButton;
    @FXML
    private ListView<String> vehicleListView;
    @FXML
    private RadioButton yearRadioButton;
    @FXML
    private RadioButton monthRadioButton;

    // Fields for Customer tab
    @FXML
    private TextField ctmakeField;
    @FXML
    private TextField ctmodelField;
    @FXML
    private Button ctSearchButton;
    @FXML
    private ListView<String> customerListView;

    // Fields for Top Sellers tab
    @FXML
    private RadioButton topRadioButton;
    @FXML
    private RadioButton usedRadioButton;
    @FXML
    private Button topSellersButton;
    @FXML
    private ListView<String> topSellersListView;

    // Fields for Locations Tab
    @FXML
    private Button locationUpdateButton ;
    @FXML
    private ListView<String> locationListView ;

    // Fields for Employee tab
    @FXML
    private Button employeeUpdateButton ;
    @FXML
    private ListView<String> employeeViewList ;

    private ToggleGroup vehicleTypeToggleGroup;
    private ToggleGroup newUsedToggleGroup;

    // Database info for connection
    private final String DB_URL = "jdbc:mysql://database-2.cns6g8eseo17.us-east-2.rds.amazonaws.com:3306/FalconSportsCar?useLegacyDatetimeCode=false&serverTimezone=America/New_York";
    private final String DB_USER = "admin";
    private final String DB_PASSWORD = "password";

    @FXML
    public void initialize() {
        setupToggleGroups();
        setupEventHandlers();
    }

    private void setupToggleGroups() {
        // Set up ToggleGroup for Vehicle Trends
        vehicleTypeToggleGroup = new ToggleGroup();
        yearRadioButton.setToggleGroup(vehicleTypeToggleGroup);
        monthRadioButton.setToggleGroup(vehicleTypeToggleGroup);
        yearRadioButton.setSelected(true);

        // Set up ToggleGroup for new/used selection
        newUsedToggleGroup = new ToggleGroup();
        topRadioButton.setToggleGroup(newUsedToggleGroup);
        usedRadioButton.setToggleGroup(newUsedToggleGroup);
        topRadioButton.setSelected(true);
    }

    private void clearAndPopulateListView(ListView<String> listView, String header, String... items) {
        listView.getItems().clear();
        listView.getItems().add(header);
        if (items.length > 0) {
            for (String item : items) {
                listView.getItems().add(item);
            }
        }
    }

    private String getVehicleSearchQuery(String timePeriod) {
        return "SELECT YEAR(r.dateOfPurchase) AS purchaseYear, " +
                (timePeriod.equals("Monthly") ? "MONTH(r.dateOfPurchase) AS purchaseMonth, " : "") +
                "COUNT(r.stockNumber) AS numSales, SUM(i.netSalePrice) AS totalSales " +
                "FROM records r " +
                "JOIN inventory i ON r.stockNumber = i.stockNumber " +
                "WHERE i.make = ? AND i.model = ? " +
                (timePeriod.equals("Yearly") ? "GROUP BY purchaseYear " : "GROUP BY purchaseYear, purchaseMonth ") +
                "ORDER BY purchaseYear" + (timePeriod.equals("Monthly") ? ", purchaseMonth" : "");
    }

    private void setupEventHandlers() {
        vSearchButton.setOnAction(event -> handleVehicleSearch());
        ctSearchButton.setOnAction(event -> handleCustomerSearch());
        topSellersButton.setOnAction(event -> handleTopSellersSearch());
        locationUpdateButton.setOnAction(event -> handleLocationSearch() );
        employeeUpdateButton.setOnAction(event -> handleEmployeeUpdate() );
    }

    @FXML
    private void handleEmployeeUpdate() {

    }

    @FXML
    private void handleLocationSearch() {
        String header = "Dealership Locations by Total Sales in the Past Year:";
        List<String> locationData = new ArrayList<>();

        // Query to get dealership locations and their total sales in the past year
        String query = "SELECT l.locationID, l.address, l.city, l.state, SUM(i.netSalePrice) AS totalSales " +
                "FROM records r " +
                "JOIN inventory i ON r.stockNumber = i.stockNumber " +
                "JOIN location l ON r.locationID = l.locationID " +
                "WHERE YEAR(r.dateOfPurchase) = YEAR(CURDATE()) - 1 " +
                "GROUP BY l.locationID, l.address, l.city, l.state " +
                "ORDER BY totalSales DESC;";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            // Process and collect the results
            while (rs.next()) {
                String locationInfo = "Location ID: " + rs.getInt("locationID") +
                        ", Address: " + rs.getString("address") +
                        ", City: " + rs.getString("city") +
                        ", State: " + rs.getString("state") +
                        ", Total Sales: $" + rs.getDouble("totalSales");
                locationData.add(locationInfo);
            }

            // Display the data in the ListView
            clearAndPopulateListView(locationListView, header, locationData.toArray(new String[0]));

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Error accessing the database");
            alert.setContentText("An error occurred while querying the database: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void handleVehicleSearch() {
        String userMake = makeField.getText().trim();
        String userModel = modelField.getText().trim();
        String timePeriod = yearRadioButton.isSelected() ? "Yearly" : "Monthly";

        if (userMake.isEmpty() || userModel.isEmpty()) {
            // Create and display an error dialog
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText("Missing Information");
            alert.setContentText("Please fill in both 'Make' and 'Model' fields before searching.");
            alert.showAndWait();
            return; // Exit the method early if input is invalid
        }

        // Prepare the header for the list view
        String header = "Vehicle Search Results for Make: " + userMake + ", Model: " + userModel + ", Time Period: " + timePeriod;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(getVehicleSearchQuery(timePeriod))) {

            pstmt.setString(1, userMake);
            pstmt.setString(2, userModel);

            ResultSet rs = pstmt.executeQuery();

            // Clear and populate the ListView with results
            vehicleListView.getItems().clear();
            vehicleListView.getItems().add(header);

            while (rs.next()) {
                String result;
                if (timePeriod.equals("Yearly")) {
                    int year = rs.getInt("purchaseYear");
                    int totalSales = rs.getInt("totalSales");
                    result = "Year: " + year + ", Total Sales: $" + totalSales;
                } else {
                    int year = rs.getInt("purchaseYear");
                    int month = rs.getInt("purchaseMonth");
                    int totalSales = rs.getInt("totalSales");
                    result = "Date: " + year + "-" + String.format("%02d", month) + ", Total Sales: $" + totalSales;
                }
                vehicleListView.getItems().add(result);
            }

            if (vehicleListView.getItems().size() == 1) {
                // If only the header is present, no data was found
                vehicleListView.getItems().add("No data found for the specified make, model, and time period.");
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Error accessing the database");
            alert.setContentText("An error occurred while querying the database: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCustomerSearch() {
        String make = ctmakeField.getText().trim();
        String model = ctmodelField.getText().trim();

        if (make.isEmpty() || model.isEmpty()) {
            // Create and display an error dialog
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText("Missing Information");
            alert.setContentText("Please fill in both 'Make' and 'Model' fields before searching.");
            alert.showAndWait();
            return; // Exit the method early if input is invalid
        }

        String query = "SELECT p.personID, p.name, p.email, p.phoneNum, p.address, p.city, p.state, p.zipcode " +
                "FROM customer c " +
                "JOIN person p ON c.personID = p.personID " +
                "JOIN records r ON c.personID = r.customerID " +
                "JOIN inventory i ON r.stockNumber = i.stockNumber " +
                "WHERE i.make = ? AND i.model = ?";

        clearAndPopulateListView(customerListView, "Customer Search Results for Make: " + make + ", Model: " + model);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, make);
            pstmt.setString(2, model);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String customerInfo = "ID: " + rs.getLong("personID") + // Change to getLong()
                        "\nName: " + rs.getString("name") +
                        "\nEmail: " + rs.getString("email") +
                        "\nPhone: " + rs.getLong("phoneNum") + // Change to getLong() if needed
                        "\nAddress: " + rs.getString("address") +
                        "\nCity: " + rs.getString("city") +
                        "\nState: " + rs.getString("state") +
                        "\nZipcode: " + rs.getInt("zipcode") +
                        "\n\n"; // Double newline for spacing between entries
                customerListView.getItems().add(customerInfo);
            }

            if (customerListView.getItems().size() == 1) {
                // If only the header is present, no data was found
                customerListView.getItems().add("No customers found for the specified make and model.");
            }

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Error accessing the database");
            alert.setContentText("An error occurred while querying the database: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTopSellersSearch() {
        topSellersListView.getItems().clear();

        String query;
        String header;

        if (topRadioButton.isSelected()) {
            // Query for top 5 best-selling cars regardless of type
            query = "SELECT i.make, i.model, COUNT(r.stockNumber) AS salesCount " +
                    "FROM records r " +
                    "JOIN inventory i ON r.stockNumber = i.stockNumber " +
                    "GROUP BY i.make, i.model " +
                    "ORDER BY salesCount DESC " +
                    "LIMIT 5";
            header = "Top 5 Best-Selling Cars:";
        } else if (usedRadioButton.isSelected()) {
            // Query for all used cars ordered by sales popularity
            query = "SELECT i.make, i.model, COUNT(r.stockNumber) AS salesCount " +
                    "FROM records r " +
                    "JOIN inventory i ON r.stockNumber = i.stockNumber " +
                    "WHERE i.carCondition = 'Used' " +
                    "GROUP BY i.make, i.model " +
                    "ORDER BY salesCount DESC";
            header = "Used Cars by Popularity:";
        } else {
            // Handle case where neither radio button is selected (optional)
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Selection Error");
            alert.setHeaderText("No Option Selected");
            alert.setContentText("Please select 'Top' or 'Used' before searching.");
            alert.showAndWait();
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            clearAndPopulateListView(topSellersListView, header);

            while (rs.next()) {
                String make = rs.getString("make");
                String model = rs.getString("model");
                int salesCount = rs.getInt("salesCount");

                String result = make + " " + model + " - " + salesCount + " units sold";
                topSellersListView.getItems().add(result);
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Error accessing the database");
            alert.setContentText("An error occurred while querying the database: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }
}