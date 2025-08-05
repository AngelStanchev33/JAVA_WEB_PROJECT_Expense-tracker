package com.example.expense_tracker.service.impl;

import com.example.expense_tracker.model.event.UserRegisteredEvent;
import com.example.expense_tracker.service.EmailService;
import com.example.expense_tracker.service.UserActivationService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class UserActivationServiceImpl implements UserActivationService {
    private final EmailService emailService;

    public UserActivationServiceImpl(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    @EventListener
    public void userRegistered(UserRegisteredEvent event) {
        emailService.sendRegistrationEmail(
                event.getUserEmail(),
                event.getUserNames());
    }

}
