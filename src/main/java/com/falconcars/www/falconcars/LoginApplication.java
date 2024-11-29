package com.falconcars.www.falconcars;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;


public class LoginApplication extends Application {

    @FXML
    private Button loginButton;

    private final String styleSheet = "login-view.fxml" ;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(styleSheet));
        System.out.println(getClass().getResource(styleSheet));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        root.requestFocus();
        primaryStage.setTitle("Employee Login");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

}
