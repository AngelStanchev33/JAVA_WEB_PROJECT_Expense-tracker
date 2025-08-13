package com.example.expense_tracker.service;

import java.math.BigDecimal;

public interface BudgetCalculationService {

    void calculateBudget(String userEmail, Long expenseId, String month, BigDecimal amount);


}
