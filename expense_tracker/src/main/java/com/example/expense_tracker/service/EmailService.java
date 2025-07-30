package com.example.expense_tracker.service;

public interface EmailService {

    void sendRegistrationEmail(
            String userEmail,
            String userName,
            String activationCode
    );
}
