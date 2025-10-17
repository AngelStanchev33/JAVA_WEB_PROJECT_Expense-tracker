package com.example.expense_tracker.exception;

public class BudgetNotFoundException extends RuntimeException {
    
    public BudgetNotFoundException() {
        super("Budget not found");
    }
    
    public BudgetNotFoundException(Long id) {
        super("Budget not found with id: " + id);
    }
    
    public BudgetNotFoundException(String userEmailAndMonth) {
        super("Budget not found for: " + userEmailAndMonth);
    }
}