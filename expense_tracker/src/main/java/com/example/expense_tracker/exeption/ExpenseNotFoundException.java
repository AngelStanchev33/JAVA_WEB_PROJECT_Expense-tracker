package com.example.expense_tracker.exeption;

public class ExpenseNotFoundException extends  RuntimeException{
    public ExpenseNotFoundException(Long id) {
        super("Expense not found with id: " + id);
    }

}
