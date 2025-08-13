package com.example.expense_tracker.service;

import com.example.expense_tracker.model.dto.ExpenseResponseDto;

public interface EventPublishingService {

    void publishExpenseCreatedEvent(ExpenseResponseDto expenseResponseDto);
}
