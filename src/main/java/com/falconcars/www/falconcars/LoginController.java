package com.falconcars.www.falconcars;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private PasswordField passwordField;

    private final String DB_STYLESHEET = "styles.css";
    private final String DB_URL = DatabaseConfig.getDbUrl(); // Static call
    private final String DB_USER = DatabaseConfig.getDbUser(); // Static call
    private final String DB_PASSWORD = DatabaseConfig.getDbPassword(); // Static call

    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    @FXML
    private void handleLoginAction(ActionEvent event) {
        String employeeID = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (employeeID.isEmpty() || employeeID.equalsIgnoreCase("Enter Employee Username")) {
            showAlert("Input Error", "Please enter an employee login.");
            return;
        }

        if (password.isEmpty() || password.equalsIgnoreCase("Enter Employee Password")) {
            showAlert("Input Error", "Please enter employee password.");
            return;
        }

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
                int personID = resultSet.getInt("personID");
                String locationID = resultSet.getString("locationID");
                boolean isManager = resultSet.getBoolean("isManager");
                String userType = isManager ? "Manager" : "Sales Person";

                DBUser dbUser = new DBUser(personID, locationID, isManager);

                switchToMainView(event, dbUser);

                showAlert("Login Successful", "Welcome, " + resultSet.getString("name") + "! You are logged in as a " + userType + ".");
            } else {
                showAlert("Login Failed", "Employee ID or password is incorrect.");
                usernameField.setText("");
                passwordField.setText("");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Database connection error", e);
            showAlert("Database Error", "Unable to connect to the database.");
        }
    }

    private void switchToMainView(ActionEvent event, DBUser dbUser) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(DB_STYLESHEET));
            Parent mainRoot = loader.load();

            DatabaseController dbController = loader.getController();
            dbController.setDBUser(dbUser);

            Scene mainScene = new Scene(mainRoot);
            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            primaryStage.setScene(mainScene);
            primaryStage.show();
            primaryStage.setResizable(true);
            primaryStage.centerOnScreen();
            mainRoot.requestFocus();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading main view", e);
            showAlert("Error", "Unable to load the main view.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");
        alert.showAndWait();
    }
}