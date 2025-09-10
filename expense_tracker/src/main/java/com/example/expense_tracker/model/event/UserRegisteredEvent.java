package com.example.expense_tracker.model.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserRegisteredEvent {
    private final String userEmail;
    private final String userNames;

    public UserRegisteredEvent(String userEmail, String userNames) {
        this.userEmail = userEmail;
        this.userNames = userNames;
    }

}
