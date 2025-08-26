package com.example.expense_tracker.listener;

import com.example.expense_tracker.model.event.UserRegisteredEvent;
import com.example.expense_tracker.service.EmailService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class UserActivationListener {
    private final EmailService emailService;

    public UserActivationListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @EventListener
    public void userRegistered(UserRegisteredEvent event) {
        try {
            emailService.sendRegistrationEmail(
                    event.getUserEmail(),
                    event.getUserNames());
        } catch (Exception e) {
            System.err.println("Failed to send registration email to " + event.getUserEmail() + ": " + e.getMessage());
        }
    }
}
