package com.db_group_three.www.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
    private TextField loginField;
    @FXML
    private Button loginButton;

    private static final String DB_URL = "jdbc:mysql://database-2.cns6g8eseo17.us-east-2.rds.amazonaws.com:3306/database-2";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "password";


    // We should consider making this a boolean, or int return
    // to confirm a successful login.
    @FXML
    private void handleLoginAction() {
        String employeeID = loginField.getText().trim();

        if (employeeID.isEmpty()) {
            showAlert("Input Error", "Please enter an employee number.");
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM employees WHERE employee_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, employeeID);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                showAlert("Login Successful", "Welcome, " + resultSet.getString("name") + "!");
            } else {
                showAlert("Login Failed", "Employee ID not found.");
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