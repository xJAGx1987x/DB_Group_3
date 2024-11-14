package com.db_group_three.www.demo;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.control.Alert.AlertType;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {
    @FXML
    private Pane loginPane;
    @FXML
    private Label falconLabel;
    @FXML
    private TextField usernameField;
    @FXML
    private Button loginButton;
    @FXML
    private PasswordField passwordField ;

    private final String DB_URL = "jdbc:mysql://database-2.cns6g8eseo17.us-east-2.rds.amazonaws.com:3306/FalconSportsCar?useLegacyDatetimeCode=false&serverTimezone=America/New_York";
    private final String DB_USER = "admin";
    private final String DB_PASSWORD = "password";


    // We should consider making this a boolean, or int return
    // to confirm a successful login.
    @FXML
    private void handleLoginAction() {
        String employeeID = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (employeeID.isEmpty() || employeeID.equalsIgnoreCase("Enter a Username")) {
            showAlert("Input Error", "Please enter an employee number.");
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Updated query to join with location_employee to determine if the user is a manager
            String query = "SELECT p.name, s.personID, le.isManager " +
                    "FROM sales_person s " +
                    "JOIN person p ON s.personID = p.personID " +
                    "JOIN location_employee le ON s.personID = le.personID " +
                    "WHERE s.username = ? AND s.password = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, employeeID);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int personID = resultSet.getInt("personID");
                boolean isManager = resultSet.getBoolean("isManager");
                String userType = isManager ? "Manager" : "Employee";

                showAlert("Login Successful", "Welcome, " + resultSet.getString("name") + "! You are logged in as a " + userType + ".");
            } else {
                showAlert("Login Failed", "Employee ID or password is incorrect.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Unable to connect to the database.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}