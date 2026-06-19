package com.pos.view;

import com.pos.model.User;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainController {
    private final Stage stage;
    private final User currentUser;
    private BorderPane root;

    public MainController(Stage stage, User user) {
        this.stage = stage;
        this.currentUser = user;
    }

    public Scene getScene() {
        root = new BorderPane();
        root.setLeft(buildNav());
        showPOS();
        return new Scene(root);
    }

    private VBox buildNav() {
        Label userLabel = new Label(currentUser.getFullName());
        userLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");
        Label roleLabel = new Label(currentUser.getRole());
        roleLabel.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 11px;");

        Button posBtn  = navButton("POS Register");
        Button retBtn  = navButton("Process Return");
        Button invBtn  = navButton("Inventory");
        Button repBtn  = navButton("Reports");
        Button admBtn  = navButton("Manage Users");
        Button logBtn  = navButton("Logout");
        Button quitBtn = navButton("Quit");

        posBtn.setOnAction(e -> showPOS());
        retBtn.setOnAction(e -> showReturns());
        invBtn.setOnAction(e -> showInventory());
        repBtn.setOnAction(e -> showReports());
        admBtn.setOnAction(e -> showAdmin());
        logBtn.setOnAction(e -> logout());
        quitBtn.setOnAction(e-> javafx.application.Platform.exit());

        if (currentUser.getRole().equals("CASHIER")) {
            invBtn.setDisable(true);
            repBtn.setDisable(true);
            admBtn.setDisable(true);
        }
        if (currentUser.getRole().equals("MANAGER")) {
            admBtn.setDisable(true);
        }

        VBox nav = new VBox(8, userLabel, roleLabel, posBtn, retBtn, invBtn, repBtn, admBtn, logBtn, quitBtn);
        nav.setPadding(new Insets(16));
        nav.setStyle("-fx-background-color: #2c3e50; -fx-min-width: 160px;");
        return nav;
    }

    private Button navButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: center-left; -fx-padding: 8;");
        return btn;
    }

    private void showPOS()       { root.setCenter(new POSView(currentUser).getView()); }
    private void showReturns()   { root.setCenter(new ReturnView(currentUser).getView()); }
    private void showInventory() { root.setCenter(new InventoryView().getView()); }
    private void showReports()   { root.setCenter(new ReportView().getView()); }
    private void showAdmin()     { root.setCenter(new AdminView().getView()); }

    private void logout() {
        stage.setScene(new LoginController().getScene());
        javafx.application.Platform.runLater(() -> {
            stage.setMaximized(false);
            stage.setMaximized(true);
        });
    }
}