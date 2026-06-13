package com.pos.controller;

import com.pos.dao.UserDAO;
import com.pos.model.User;

public class AuthController {

    private final UserDAO userDAO = new UserDAO();
    private User currentUser;

    public User login(String employeeID, String pin) {
        if (employeeID == null || employeeID.trim().isEmpty() ||
            pin == null || pin.trim().isEmpty()) return null;

        User user = userDAO.findByEmployeeID(employeeID.trim());
        if (user == null) return null;
        if (!user.getEmployeePin().equals(pin.trim())) return null;
        if (!user.isActive()) return null;

        currentUser = user;
        return user;
    }

    public void logout() { currentUser = null; }
    public User getCurrentUser() { return currentUser; }
    public boolean isLoggedIn() { return currentUser != null; }
}