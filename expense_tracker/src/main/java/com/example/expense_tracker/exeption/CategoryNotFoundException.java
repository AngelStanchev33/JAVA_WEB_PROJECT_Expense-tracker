package com.example.expense_tracker.exeption;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(String category) {
        super("Category not found: " + category);
    }
}
