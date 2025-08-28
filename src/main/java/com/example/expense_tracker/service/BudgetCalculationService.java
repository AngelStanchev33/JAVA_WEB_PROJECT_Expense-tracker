package com.example.expense_tracker.service;

import java.math.BigDecimal;

public interface BudgetCalculationService {

    void calculateBudgetWhenExpenseIsCreated(String userEmail, Long expenseId, String month);
    void calculateBudgetWhenBudgetIsCreated(Long id);

}
