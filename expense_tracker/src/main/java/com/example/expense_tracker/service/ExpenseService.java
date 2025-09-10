package com.example.expense_tracker.service;

import com.example.expense_tracker.model.dto.CreateExpenseDto;
import com.example.expense_tracker.model.dto.ExpenseResponseDto;
import com.example.expense_tracker.model.dto.UpdateExpenseDto;

import java.util.List;

public interface ExpenseService {

    ExpenseResponseDto createExpense(CreateExpenseDto createExpenseDto, String email);

    List<ExpenseResponseDto> getUserExpenses(String userEmail);

    ExpenseResponseDto getExpenseById(Long id);

    ExpenseResponseDto updateExpense(Long id, UpdateExpenseDto updateExpenseDto);

    void deleteExpense(Long expenseId);

    boolean isOwner(Long id, String username);
}
