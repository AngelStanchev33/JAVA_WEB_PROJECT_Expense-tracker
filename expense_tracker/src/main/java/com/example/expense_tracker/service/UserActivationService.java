package com.example.expense_tracker.service;

import com.example.expense_tracker.model.event.UserRegisteredEvent;

public interface UserActivationService {

    void userRegistered(UserRegisteredEvent event);

}
