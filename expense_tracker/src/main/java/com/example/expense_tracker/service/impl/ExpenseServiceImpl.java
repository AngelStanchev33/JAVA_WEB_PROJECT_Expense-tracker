package com.example.expense_tracker.service.impl;

import com.example.expense_tracker.model.dto.CreateExpenseDto;
import com.example.expense_tracker.model.dto.ExpenseResponseDto;
import com.example.expense_tracker.service.ExpenseService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseServiceImpl implements ExpenseService {
    @Override
    public ExpenseResponseDto createExpense(CreateExpenseDto createExpenseDto, String email) {
        return null;
    }

    @Override
    public List<ExpenseResponseDto> getUserExpenses(String userEmail) {
        return null;
    }
}
