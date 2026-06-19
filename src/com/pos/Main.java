package com.pos;

import com.pos.dao.DatabaseManager;
import com.pos.view.LoginController;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        DatabaseManager.getInstance().initializeDatabase();

        LoginController loginController = new LoginController();
        primaryStage.setTitle("POS System");
        primaryStage.setScene(loginController.getScene());
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}