package com.db_group_three.www.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class DatabaseController {

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
    // Fields for new/used vehicle selection
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

    @FXML
    public void initialize() {
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

        // Add action listeners to search buttons
        vSearchButton.setOnAction(event -> handleVehicleSearch());
        ctSearchButton.setOnAction(event -> handleCustomerSearch());
        topSellersButton.setOnAction(event -> handleTopSellersSearch());
    }

    @FXML
    private void handleVehicleSearch() {
        String make = makeField.getText();
        String model = modelField.getText();
        String timePeriod = yearRadioButton.isSelected() ? "Yearly" : "Monthly";

        vehicleListView.getItems().clear();
        vehicleListView.getItems().add("Vehicle Search Results:");
        vehicleListView.getItems().add("Make: " + make);
        vehicleListView.getItems().add("Model: " + model);
        vehicleListView.getItems().add("Time Period: " + timePeriod);
    }

    @FXML
    private void handleCustomerSearch() {
        String make = ctmakeField.getText();
        String model = ctmodelField.getText();

        customerListView.getItems().clear();
        customerListView.getItems().add("Customer Search Results:");
        customerListView.getItems().add("Make: " + make);
        customerListView.getItems().add("Model: " + model);
    }

    @FXML
    private void handleTopSellersSearch() {
        // Logic to display top 5 vehicles based on the number sold in the past year
        topSellersListView.getItems().clear();
        topSellersListView.getItems().add("Top 5 Vehicles Sold in the Past Year:");
        // Placeholder data for demonstration
        topSellersListView.getItems().add("1. Vehicle A - 500 units");
        topSellersListView.getItems().add("2. Vehicle B - 450 units");
        topSellersListView.getItems().add("3. Vehicle C - 400 units");
        topSellersListView.getItems().add("4. Vehicle D - 350 units");
        topSellersListView.getItems().add("5. Vehicle E - 300 units");
    }
}