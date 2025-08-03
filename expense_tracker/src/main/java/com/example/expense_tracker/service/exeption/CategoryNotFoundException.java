package com.example.expense_tracker.service.exeption;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(String category) {
        super("Category not found" + category);
    }
}
