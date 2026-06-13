package com.pos.dao;

import com.pos.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private final DatabaseManager dbManager = DatabaseManager.getInstance();

    public User findByEmployeeID(String employeeID) {
        String sql = "SELECT * FROM users WHERE employeeID = ? AND active = 1";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, employeeID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("UserDAO.findByEmployeeID error: " + e.getMessage());
        }
        return null;
    }

    public boolean create(User user) {
        String sql = "INSERT INTO users (employeeID, firstName, lastName, role, active, employeePin) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, user.getEmployeeID());
            stmt.setString(2, user.getFirstName());
            stmt.setString(3, user.getLastName());
            stmt.setString(4, user.getRole());
            stmt.setInt(5, user.isActive() ? 1 : 0);
            stmt.setString(6, user.getEmployeePin());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("UserDAO.create error: " + e.getMessage());
            return false;
        }
    }

    public boolean update(User user) {
        String sql = "UPDATE users SET firstName = ?, lastName = ?, role = ?, active = ?, employeePin = ? WHERE employeeID = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getRole());
            stmt.setInt(4, user.isActive() ? 1 : 0);
            stmt.setString(5, user.getEmployeePin());
            stmt.setString(6, user.getEmployeeID());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("UserDAO.update error: " + e.getMessage());
            return false;
        }
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Statement stmt = dbManager.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                users.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("UserDAO.findAll error: " + e.getMessage());
        }
        return users;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("id"),
            rs.getString("employeeID"),
            rs.getString("firstName"),
            rs.getString("lastName"),
            rs.getString("role"),
            rs.getInt("active") == 1,
            rs.getString("employeePin")
        );
    }
}