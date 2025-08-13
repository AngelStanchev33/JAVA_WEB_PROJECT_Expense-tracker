package com.example.expense_tracker.service;

import com.example.expense_tracker.model.dto.BudgetResponseDto;
import com.example.expense_tracker.model.dto.CreateBudgetDto;
import com.example.expense_tracker.model.dto.UpdateBudgetDto;

import java.util.List;

public interface BudgetService {
    BudgetResponseDto createBudget(CreateBudgetDto createBudgetDto, String email);

    BudgetResponseDto updateBudget(Long budgetOd, UpdateBudgetDto updateBudgetDto);

    List<BudgetResponseDto> getUserBudgets(String email);

}
