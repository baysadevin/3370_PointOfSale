package com.pos.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static DatabaseManager instance;
    private Connection connection;
    private static final String DB_URL = "jdbc:sqlite:pos.db";

    private DatabaseManager() {}

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
            connection.createStatement()
                      .execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    public void initializeDatabase() {
        try (Statement stmt = getConnection().createStatement()) {
            stmt.executeUpdate(CREATE_USERS);
            stmt.executeUpdate(CREATE_PRODUCTS);
            stmt.executeUpdate(CREATE_TRANSACTIONS);
            stmt.executeUpdate(CREATE_TRANSACTION_ITEMS);
            stmt.executeUpdate(CREATE_INVENTORY_ALERTS);
            stmt.executeUpdate(CREATE_PURCHASE_ORDERS);
            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }

    private static final String CREATE_USERS =
        "CREATE TABLE IF NOT EXISTS users (" +
        "    id          INTEGER PRIMARY KEY AUTOINCREMENT," +
        "    employeeID TEXT    NOT NULL UNIQUE," +
        "    firstName  TEXT    NOT NULL," +
        "    lastName   TEXT    NOT NULL," +
        "    role        TEXT    NOT NULL CHECK(role IN ('CASHIER','MANAGER','ADMIN'))," +
        "    active      INTEGER NOT NULL DEFAULT 1," +
        "    employeePin    TEXT    NOT NULL," +
        "    created_at  TEXT    DEFAULT (datetime('now', 'localtime'))" +
        ");";

    private static final String CREATE_PRODUCTS =
        "CREATE TABLE IF NOT EXISTS products (" +
        "    id            INTEGER PRIMARY KEY AUTOINCREMENT," +
        "    barcode       TEXT    NOT NULL UNIQUE," +
        "    name          TEXT    NOT NULL," +
        "    price         REAL    NOT NULL," +
        "    stockQuantity     INTEGER NOT NULL DEFAULT 0," +
        "    lowThreshold INTEGER NOT NULL DEFAULT 10," +
        "    active        INTEGER NOT NULL DEFAULT 1" +
        ");";

    private static final String CREATE_TRANSACTIONS =
        "CREATE TABLE IF NOT EXISTS transactions (" +
        "    id              INTEGER PRIMARY KEY AUTOINCREMENT," +
        "    employeeID    TEXT    NOT NULL," +
        "    type            TEXT    NOT NULL CHECK(type IN ('SALE','RETURN'))," +
        "    payment_method  TEXT    NOT NULL CHECK(payment_method IN ('CASH','CARD','GIFT_CARD'))," +
        "    subtotal        REAL    NOT NULL," +
        "    tax_amount      REAL    NOT NULL," +
        "    total           REAL    NOT NULL," +
        "    amount_tendered REAL," +
        "    change_given    REAL," +
        "    returned        INTEGER NOT NULL DEFAULT 0," +
        "    created_at      TEXT    DEFAULT (datetime('now', 'localtime'))," +
        "    FOREIGN KEY (employeeID) REFERENCES users(employeeID)" +
        ");";

    private static final String CREATE_TRANSACTION_ITEMS =
        "CREATE TABLE IF NOT EXISTS transaction_items (" +
        "    id             INTEGER PRIMARY KEY AUTOINCREMENT," +
        "    transaction_id INTEGER NOT NULL," +
        "    product_id     INTEGER NOT NULL," +
        "    quantity       INTEGER NOT NULL," +
        "    unit_price     REAL    NOT NULL," +
        "    line_total     REAL    NOT NULL," +
        "    FOREIGN KEY (transaction_id) REFERENCES transactions(id)," +
        "    FOREIGN KEY (product_id)     REFERENCES products(id)" +
        ");";

    private static final String CREATE_INVENTORY_ALERTS =
        "CREATE TABLE IF NOT EXISTS inventory_alerts (" +
        "    id         INTEGER PRIMARY KEY AUTOINCREMENT," +
        "    product_id INTEGER NOT NULL," +
        "    message    TEXT    NOT NULL," +
        "    resolved   INTEGER NOT NULL DEFAULT 0," +
        "    created_at TEXT    DEFAULT (datetime('now'))," +
        "    FOREIGN KEY (product_id) REFERENCES products(id)" +
        ");";

    private static final String CREATE_PURCHASE_ORDERS =
        "CREATE TABLE IF NOT EXISTS purchase_orders (" +
        "    id         INTEGER PRIMARY KEY AUTOINCREMENT," +
        "    product_id INTEGER NOT NULL," +
        "    quantity   INTEGER NOT NULL," +
        "    status     TEXT    NOT NULL DEFAULT 'PENDING'," +
        "    created_at TEXT    DEFAULT (datetime('now'))," +
        "    FOREIGN KEY (product_id) REFERENCES products(id)" +
        ");";
}