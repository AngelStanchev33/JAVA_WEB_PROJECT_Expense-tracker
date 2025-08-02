package com.example.expense_tracker.service;

import com.example.expense_tracker.model.dto.CreateExpenseDto;
import com.example.expense_tracker.model.dto.ExpenseResponseDto;

import java.util.List;

public interface ExpenseService {

    public ExpenseResponseDto createExpense(CreateExpenseDto createExpenseDto, String email);

    List<ExpenseResponseDto> getUserExpenses(String userEmail);
}
