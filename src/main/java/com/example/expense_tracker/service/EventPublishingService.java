package com.example.expense_tracker.service;

import com.example.expense_tracker.model.dto.BudgetResponseDto;
import com.example.expense_tracker.model.dto.CreateBudgetDto;
import com.example.expense_tracker.model.dto.ExpenseResponseDto;

public interface EventPublishingService {

    void publishExpenseCreatedEvent(ExpenseResponseDto expenseResponseDto);

    void publishBudgetCreatedEvent(BudgetResponseDto budgetResponseDto);
}
