package com.example.expense_tracker.service.impl;

import com.example.expense_tracker.model.dto.ExpenseResponseDto;
import com.example.expense_tracker.model.event.ExpenseCreatedEvent;
import com.example.expense_tracker.service.EventPublishingService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EventPublishingServiceImpl implements EventPublishingService {

    private final ApplicationEventPublisher applicationEventPublisher;

    public EventPublishingServiceImpl(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publishExpenseCreatedEvent(ExpenseResponseDto expenseResponseDto) {
        applicationEventPublisher.publishEvent(new ExpenseCreatedEvent(
                expenseResponseDto.getId(),
                expenseResponseDto.getUser(),
                expenseResponseDto.getAmount(),
                expenseResponseDto.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM"))
        ));

    }
}
