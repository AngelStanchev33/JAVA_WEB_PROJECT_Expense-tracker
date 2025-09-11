package com.example.expense_tracker.service;

import com.example.expense_tracker.model.dto.NotificationResponseDto;

import java.util.List;

public interface NotificationService {
    List<NotificationResponseDto> getAllNotifications(String email);
}


