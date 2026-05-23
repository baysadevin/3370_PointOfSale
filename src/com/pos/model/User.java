package com.pos.model;

public class User {
    private int id;
    private String employeeID;
    private String firstName;
    private String lastName;
    private String role;
    private boolean active;
    private String employeePin;

    public User(int id, String employeeID, String firstName, String lastName, String role, boolean active, String employeePin) {
        this.id = id;
        this.employeeID = employeeID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.active = active;
        this.employeePin = employeePin;
    }

    // Getters
    public int getId() { return id; }
    public String getEmployeeID() { return employeeID; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getRole() { return role; }
    public boolean isActive() { return active; }
    public String getEmployeePin() { return employeePin; }

    // Setters
    public void setEmployeeID(String employeeID) { this.employeeID = employeeID; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setRole(String role) { this.role = role; }
    public void setActive(boolean active) { this.active = active; }
    public void setEmployeePin(String employeePin) { this.employeePin = employeePin; }


}