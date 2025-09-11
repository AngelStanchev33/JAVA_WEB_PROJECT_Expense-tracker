package com.example.expense_tracker.web;

import com.example.expense_tracker.model.dto.NotificationResponseDto;
import com.example.expense_tracker.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/")
    public ResponseEntity<List<NotificationResponseDto>> getAllNotifications(Authentication authentication) {

        return ResponseEntity.ok(notificationService.getAllNotifications(authentication.getName()));
    }
}
