package com.example.expense_tracker.web;

import com.example.expense_tracker.model.dto.CreateExpenseDto;
import com.example.expense_tracker.model.dto.ExpenseResponseDto;
import com.example.expense_tracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ResponseEntity<ExpenseResponseDto> createExpense(
            @Valid @RequestBody CreateExpenseDto dto,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        ExpenseResponseDto response = expenseService.createExpense(dto, userEmail);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponseDto>> getUserExpenses(Authentication authentication) {
        String userEmail = authentication.getName();
        List<ExpenseResponseDto> expenses = expenseService.getUserExpenses(userEmail);
        
        return ResponseEntity.ok(expenses);
    }
}