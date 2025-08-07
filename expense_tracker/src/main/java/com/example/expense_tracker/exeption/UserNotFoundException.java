package com.example.expense_tracker.exeption;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String email) {
        super("User not found with email: " + email);
    }
}
