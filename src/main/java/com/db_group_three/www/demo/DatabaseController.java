package com.db_group_three.www.demo;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.sql.*;

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
    private RadioButton newRadioButton;
    @FXML
    private RadioButton usedRadioButton;
    @FXML
    private Button topSellersButton;
    @FXML
    private ListView<String> topSellersListView;

    private ToggleGroup vehicleTypeToggleGroup;
    private ToggleGroup newUsedToggleGroup;

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
        newRadioButton.setToggleGroup(newUsedToggleGroup);
        usedRadioButton.setToggleGroup(newUsedToggleGroup);
        newRadioButton.setSelected(true);
    }

    private void setupEventHandlers() {
        vSearchButton.setOnAction(event -> handleVehicleSearch());
        ctSearchButton.setOnAction(event -> handleCustomerSearch());
        topSellersButton.setOnAction(event -> handleTopSellersSearch());
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

        vehicleListView.getItems().clear();
        vehicleListView.getItems().add("Vehicle Search Results:");
        vehicleListView.getItems().add("Make: " + userMake);
        vehicleListView.getItems().add("Model: " + userModel);
        vehicleListView.getItems().add("Time Period: " + timePeriod);

        // SQL query to retrieve relevant sales records
        String query = "SELECT YEAR(r.dateOfPurchase) AS purchaseYear, " +
                (timePeriod.equals("Monthly") ? "MONTH(r.dateOfPurchase) AS purchaseMonth, " : "") +
                "COUNT(r.stockNumber) AS numSales, SUM(i.netSalePrice) AS totalSales " +
                "FROM records r " +
                "JOIN inventory i ON r.stockNumber = i.stockNumber " +
                "WHERE i.make = ? AND i.model = ? " +
                (timePeriod.equals("Yearly") ? "GROUP BY purchaseYear " : "GROUP BY purchaseYear, purchaseMonth ") +
                "ORDER BY purchaseYear" + (timePeriod.equals("Monthly") ? ", purchaseMonth" : "");

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, userMake);
            pstmt.setString(2, userModel);

            ResultSet rs = pstmt.executeQuery();

            // Process and display results
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
        String make = ctmakeField.getText();
        String model = ctmodelField.getText();

        clearAndPopulateListView(customerListView, "Customer Search Results:",
                "Make: " + make, "Model: " + model);
    }

    @FXML
    private void handleTopSellersSearch() {
        topSellersListView.getItems().clear();
        topSellersListView.getItems().add("Top 5 Vehicles Sold in the Past Year:");
        // Placeholder data for demonstration
        addPlaceholderTopSellers();
    }

    private void clearAndPopulateListView(ListView<String> listView, String header, String... items) {
        listView.getItems().clear();
        listView.getItems().add(header);
        for (String item : items) {
            listView.getItems().add(item);
        }
    }

    private void addPlaceholderTopSellers() {
        topSellersListView.getItems().add("1. Vehicle A - 500 units");
        topSellersListView.getItems().add("2. Vehicle B - 450 units");
        topSellersListView.getItems().add("3. Vehicle C - 400 units");
        topSellersListView.getItems().add("4. Vehicle D - 350 units");
        topSellersListView.getItems().add("5. Vehicle E - 300 units");
    }
}
