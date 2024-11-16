package com.db_group_three.www.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {
    @FXML
    private AnchorPane loginPane;
    @FXML
    private Label falconLabel;
    @FXML
    private TextField usernameField;
    @FXML
    private Button loginButton;
    @FXML
    private PasswordField passwordField ;

    private final String DB_URL
            = "jdbc:mysql://database-2.cns6g8eseo17.us-east-2.rds.amazonaws.com:3306" +
            "/FalconSportsCar?useLegacyDatetimeCode=false&serverTimezone=America/New_York";
    private final String DB_USER = "admin";
    private final String DB_PASSWORD = "password";


    @FXML
    private void handleLoginAction(ActionEvent event) {
        // Retrieve the entered username and password
        String employeeID = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Validate input fields
        if (employeeID.isEmpty() || employeeID.equalsIgnoreCase("Enter Employee Username")) {
            showAlert("Input Error", "Please enter an employee number.");
            return;
        }

        if (password.isEmpty() || password.equalsIgnoreCase("Enter Employee Password")) {
            showAlert("Input Error", "Please enter employee password.");
            return;
        }

        // Database connection and login check
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
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
                // User authenticated successfully
                int personID = resultSet.getInt("personID");
                boolean isManager = resultSet.getBoolean("isManager");
                String userType = isManager ? "Manager" : "Sales Person";

                DBUser dbUser = new DBUser(personID, isManager);

                // Load the main view and pass user data to the next controller
                FXMLLoader loader = new FXMLLoader(getClass().getResource("db-view.fxml"));
                Parent mainRoot = loader.load();

                DatabaseController dbController = loader.getController();
                dbController.setDBUser(dbUser);

                // Switch to the main application view
                Scene mainScene = new Scene(mainRoot);
                Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                primaryStage.setScene(mainScene);
                primaryStage.show();
                primaryStage.setResizable(true);
                primaryStage.centerOnScreen();
                mainRoot.requestFocus();

                // Show success message
                showAlert("Login Successful", "Welcome, " + resultSet.getString("name") + "! You are logged in as a " + userType + ".");
            } else {
                // Authentication failed
                showAlert("Login Failed", "Employee ID or password is incorrect.");
                usernameField.setText("");
                passwordField.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Unable to connect to the database.");
        }
    }

    // Show alert dialog with a custom message
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");
        alert.showAndWait();
    }
}
