package com.db_group_three.www.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class DatabaseController {

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


    private ToggleGroup toggleGroup;


    // Initialize method to set up the ToggleGroup and other components

    @FXML

    public void initialize() {

        // Create and set the ToggleGroup for the radio buttons

        toggleGroup = new ToggleGroup();
        yearRadioButton.setToggleGroup(toggleGroup);
        monthRadioButton.setToggleGroup(toggleGroup);

        // Optionally set a default selection
        yearRadioButton.setSelected(true);
    }


    // Method to handle search button click
    @FXML
    private void handleSearchButtonAction() {

        String make = makeField.getText();
        String model = modelField.getText();
        String timePeriod = yearRadioButton.isSelected() ? "Yearly" : "Monthly";

        System.out.println("Search pressed");

        // Placeholder logic to display search criteria in the ListView
        vehicleListView.getItems().clear();
        vehicleListView.getItems().add("Search Results:");
        vehicleListView.getItems().add("Make: " + make);
        vehicleListView.getItems().add("Model: " + model);
        vehicleListView.getItems().add("Time Period: " + timePeriod);
    }
}