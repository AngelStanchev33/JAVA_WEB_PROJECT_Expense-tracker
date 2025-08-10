package com.example.expense_tracker.service;

import com.example.expense_tracker.model.dto.BudgetResponseDto;
import com.example.expense_tracker.model.dto.CreateBudgetDto;

import java.util.List;

public interface BudgetService {
    BudgetResponseDto createBudget(CreateBudgetDto createBudgetDto, String email);

    List<BudgetResponseDto> getUserBudgets(String email);

}
