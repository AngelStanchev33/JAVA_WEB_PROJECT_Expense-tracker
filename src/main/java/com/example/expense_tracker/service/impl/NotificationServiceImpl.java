package com.example.expense_tracker.service.impl;

import com.example.expense_tracker.model.dto.NotificationResponseDto;
import com.example.expense_tracker.model.entity.NotificationEntity;
import com.example.expense_tracker.repository.NotificationRepository;
import com.example.expense_tracker.repository.UserRepository;
import com.example.expense_tracker.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public List<NotificationResponseDto> getAllNotifications(String email) {

        return notificationRepository.findAllByUserEmail(email)
                .stream()
                .map(entity -> {
                    NotificationResponseDto notificationResponseDto = new NotificationResponseDto();
                    notificationResponseDto.setMessage(entity.getMessage());


                    return notificationResponseDto;
                })
                .toList();
    }
}
