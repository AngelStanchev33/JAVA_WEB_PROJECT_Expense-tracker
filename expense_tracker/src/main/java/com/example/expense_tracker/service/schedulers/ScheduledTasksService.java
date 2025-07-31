package com.example.expense_tracker.service.schedulers;

import com.example.expense_tracker.service.UserActivationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledTasksService {

    private final UserActivationService userActivationService;

    public ScheduledTasksService(UserActivationService userActivationService) {
        this.userActivationService = userActivationService;
    }

    @Scheduled(fixedRate = 3600000)
    public void cleanupExpiredActivationCodes() {
        userActivationService.cleanUpObsoleteActivationLinks();
    }
}