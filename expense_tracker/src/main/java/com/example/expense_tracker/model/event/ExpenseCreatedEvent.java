package com.example.expense_tracker.model.event;

import java.math.BigDecimal;
import java.time.YearMonth;

public class ExpenseCreatedEvent {

    private final Long expenseId;
    private final BigDecimal amount;
    private final YearMonth month;
    private final String categoryName;

    public ExpenseCreatedEvent(Long expenseId, BigDecimal amount, YearMonth month, String categoryName) {
        this.expenseId = expenseId;
        this.amount = amount;
        this.month = month;
        this.categoryName = categoryName;
    }
}
