package com.pos.view;

import com.pos.controller.AuthController;
import com.pos.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginController {

    private final AuthController authController = new AuthController();
    private TextField employeeIDField;
    private PasswordField pinField;
    private Label messageLabel;

    public Scene getScene() {
        Label title = new Label("Department Store POS");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Label subtitle = new Label("Workstation 01");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #888888;");

        employeeIDField = new TextField();
        employeeIDField.setPromptText("Employee ID");

        pinField = new PasswordField();
        pinField.setPromptText("PIN");

        messageLabel = new Label("");
        messageLabel.setStyle("-fx-text-fill: red;");
        messageLabel.setWrapText(true);

        Button loginButton = new Button("Sign In");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8;");
        loginButton.setOnAction(e -> handleLogin());
        pinField.setOnAction(e -> handleLogin());

        VBox form = new VBox(8,
            new Label("Employee ID"), employeeIDField,
            new Label("PIN"), pinField,
            messageLabel, loginButton);
        form.setMaxWidth(320);

        VBox root = new VBox(14, title, subtitle, form);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(60));
        root.setStyle("-fx-background-color: #f4f4f4;");

        return new Scene(root, 900, 650);
    }

    private void handleLogin() {
        String employeeID = employeeIDField.getText().trim();
        String pin = pinField.getText().trim();

        if (employeeID.isEmpty() || pin.isEmpty()) {
            showError("Employee ID and PIN are required.");
            return;
        }
        if (pin.length() != 4 || !pin.matches("\\d{4}")) {
            showError("PIN must be exactly 4 digits.");
            return;
        }

        User user = authController.login(employeeID, pin);

        if (user == null) {
            showError("Invalid Employee ID or PIN.");
            pinField.clear();
            employeeIDField.requestFocus();
        } else {
            Stage stage = (Stage) employeeIDField.getScene().getWindow();
            MainController main = new MainController(stage, user);
            stage.setScene(main.getScene());
        }
    }

    private void showError(String msg) {
        messageLabel.setStyle("-fx-text-fill: red;");
        messageLabel.setText(msg);
    }
}