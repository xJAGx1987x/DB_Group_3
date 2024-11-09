package com.db_group_three.www.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class DatabaseController {

    @FXML
    private TextField makeField;

    @FXML
    private TextField modelField;

    @FXML
    private TextField yearField;

    @FXML
    private ChoiceBox<String> colorChoiceBox;

    @FXML
    private TextField priceField;

    @FXML
    private Button vSearchButton;

    @FXML
    private ListView<String> vehicleListView;

    // Initialize method to set up the ChoiceBox and other components
    @FXML
    public void initialize() {
        // Populate the colorChoiceBox with options
        colorChoiceBox.getItems().addAll("Red", "Blue", "Green", "Black", "White", "Silver", "Yellow");
    }

    // Method to handle search button click
    @FXML
    private void handleSearchButtonAction() {
        String make = makeField.getText();
        String model = modelField.getText();
        String year = yearField.getText();
        String color = colorChoiceBox.getValue();
        String price = priceField.getText();

        // Placeholder logic to display search criteria in the ListView
        vehicleListView.getItems().clear();
        vehicleListView.getItems().add("Search Results:");
        vehicleListView.getItems().add("Make: " + make);
        vehicleListView.getItems().add("Model: " + model);
        vehicleListView.getItems().add("Year: " + year);
        vehicleListView.getItems().add("Color: " + color);
        vehicleListView.getItems().add("Price: " + price);
    }
}