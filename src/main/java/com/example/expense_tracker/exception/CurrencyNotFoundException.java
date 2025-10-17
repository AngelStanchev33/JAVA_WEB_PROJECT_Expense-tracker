package com.example.expense_tracker.exception;

public class CurrencyNotFoundException extends RuntimeException {
    public CurrencyNotFoundException(String currencyCode) {
        super("Currency not found: " + currencyCode);
    }
}