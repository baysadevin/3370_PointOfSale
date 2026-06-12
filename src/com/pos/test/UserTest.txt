package com.pos.test;

import com.pos.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private User activeUser;
    private User inactiveUser;

    @BeforeEach
    public void setUp() {
        activeUser   = new User(1, "EMP-001", "John", "Doe",   "CASHIER", true,  "1234");
        inactiveUser = new User(2, "EMP-002", "Jane", "Smith", "MANAGER", false, "5678");
    }

    // Test 1: getFullName() concatenates first and last name correctly
    @Test
    public void testGetFullName_ReturnsFullName() {
        assertEquals("John Doe", activeUser.getFullName(),
            "getFullName() should return first and last name separated by a space");
    }

    // Test 2: isActive() returns true for an active user
    @Test
    public void testIsActive_WhenUserIsActive_ReturnsTrue() {
        assertTrue(activeUser.isActive(),
            "isActive() should return true for an active user");
    }

    // Test 3: isActive() returns false for an inactive user
    @Test
    public void testIsActive_WhenUserIsInactive_ReturnsFalse() {
        assertFalse(inactiveUser.isActive(),
            "isActive() should return false for an inactive user");
    }

    // Test 4: setRole() correctly updates the user's role
    @Test
    public void testSetRole_UpdatesRoleCorrectly() {
        activeUser.setRole("ADMIN");
        assertEquals("ADMIN", activeUser.getRole(),
            "setRole() should update the role to the new value");
    }
}