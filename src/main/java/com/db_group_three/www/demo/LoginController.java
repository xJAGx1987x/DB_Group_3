package com.db_group_three.www.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
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

    @FXML
    private void handleLoginAction(ActionEvent event) {
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
                String userType = isManager ? "Manager" : "Sales Person";

                DBUser dbUser = new DBUser(personID, isManager);

                try {
                    // Load the main view
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("db-view.fxml"));
                    Parent mainRoot = loader.load();

                    // Get the controller and pass DBUser data
                    DatabaseController dbController = loader.getController();
                    dbController.setDBUser(dbUser);

                    // Set up the new scene
                    Scene mainScene = new Scene(mainRoot);
                    Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    primaryStage.setScene(mainScene);
                    primaryStage.show();
                    primaryStage.setResizable(true);
                    primaryStage.centerOnScreen();

                    // Show login success message
                    showAlert("Login Successful", "Welcome, " + resultSet.getString("name") + "! You are logged in as a " + userType + ".");
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert("Loading Error", "Unable to load the main application view.");
                }
            } else {
                showAlert("Login Failed", "Employee ID or password is incorrect.");
                usernameField.setText("");
                passwordField.setText("");
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