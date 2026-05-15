package com.pos;

import com.pos.dao.DatabaseManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        DatabaseManager.getInstance().initializeDatabase();

        Label label = new Label("POS System — Database Connected!");
        StackPane root = new StackPane(label);

        primaryStage.setTitle("POS System");
        primaryStage.setScene(new Scene(root, 900, 650));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}