package com.falconcars.www.falconcars;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginApplication extends Application {

    private static final String LG_STYLESHEET = "login-view.fxml";
    private static final Logger LOGGER = Logger.getLogger(LoginApplication.class.getName());

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LG_STYLESHEET));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            root.requestFocus();
            primaryStage.setTitle("Employee Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            primaryStage.show();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading login view", e);
        }
    }
}