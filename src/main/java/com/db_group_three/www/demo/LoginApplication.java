package com.db_group_three.www.demo;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoginApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        // Explicitly load and apply the stylesheet
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        root.requestFocus();
        primaryStage.setTitle("Employee Login");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

}
