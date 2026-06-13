package com.pos.view;

import com.pos.dao.UserDAO;
import com.pos.model.User;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;


public class AdminView {
    private final UserDAO userDAO = new UserDAO();
    private final ObservableList<User> users = FXCollections.observableArrayList();
    private TextField empIDField, firstNameField, lastNameField, pinField;
    private ComboBox<String> roleCombo;
    private Label messageLabel;
    private TableView<User> table;

    @SuppressWarnings("unchecked")
    public Node getView() {
        table = new TableView<>(users);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getId()).asObject());

        TableColumn<User, String> empCol = new TableColumn<>("Employee ID");
        empCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmployeeID()));

        TableColumn<User, String> firstCol = new TableColumn<>("First Name");
        firstCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFirstName()));

        TableColumn<User, String> lastCol = new TableColumn<>("Last Name");
        lastCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getLastName()));

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRole()));

        TableColumn<User, String> activeCol = new TableColumn<>("Active");
        activeCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().isActive() ? "Yes" : "No"));

        table.getColumns().addAll(idCol, empCol, firstCol, lastCol, roleCol, activeCol);

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                empIDField.setText(selected.getEmployeeID());
                firstNameField.setText(selected.getFirstName());
                lastNameField.setText(selected.getLastName());
                pinField.setText(selected.getEmployeePin());
                roleCombo.setValue(selected.getRole());
            }
        });

        empIDField    = new TextField(); empIDField.setPromptText("Employee ID");
        firstNameField = new TextField(); firstNameField.setPromptText("First Name");
        lastNameField  = new TextField(); lastNameField.setPromptText("Last Name");
        pinField      = new TextField(); pinField.setPromptText("4-digit PIN");

        roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("CASHIER", "MANAGER", "ADMIN");
        roleCombo.setValue("CASHIER");

        Button addBtn    = new Button("Add User");
        Button updateBtn = new Button("Update User");
        Button clearBtn  = new Button("Clear");

        addBtn.setOnAction(e -> addUser());
        updateBtn.setOnAction(e -> updateUser());
        clearBtn.setOnAction(e -> clearForm());

        messageLabel = new Label("");
        messageLabel.setWrapText(true);

        HBox formRow = new HBox(8,
            empIDField, firstNameField, lastNameField,
            pinField, roleCombo, addBtn, updateBtn, clearBtn);
        formRow.setPadding(new Insets(8, 0, 0, 0));

        VBox content = new VBox(8, table, formRow, messageLabel);
        content.setPadding(new Insets(16));
        VBox.setVgrow(table, Priority.ALWAYS);

        loadUsers();
        return content;
    }

    private void loadUsers() {
        users.clear();
        users.addAll(userDAO.findAll());
    }

    private void addUser() {
        try {
            User u = new User(0,
                empIDField.getText().trim(),
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                roleCombo.getValue(),
                true,
                pinField.getText().trim());

            if (userDAO.create(u)) {
                showMessage("User added successfully.", false);
                clearForm();
                loadUsers();
            } else {
                showMessage("Failed to add user.", true);
            }
        } catch (Exception e) {
            showMessage("Invalid input.", true);
        }
    }

    private void updateUser() {
        User selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { showMessage("Select a user to update.", true); return; }

        selected.setFirstName(firstNameField.getText().trim());
        selected.setLastName(lastNameField.getText().trim());
        selected.setRole(roleCombo.getValue());
        selected.setEmployeePin(pinField.getText().trim());

        if (userDAO.update(selected)) {
            showMessage("User updated.", false);
            loadUsers();
        } else {
            showMessage("Failed to update user.", true);
        }
    }

    private void clearForm() {
        empIDField.clear(); firstNameField.clear();
        lastNameField.clear(); pinField.clear();
        roleCombo.setValue("CASHIER");
    }

    private void showMessage(String msg, boolean error) {
        messageLabel.setStyle(error ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
        messageLabel.setText(msg);
    }
}