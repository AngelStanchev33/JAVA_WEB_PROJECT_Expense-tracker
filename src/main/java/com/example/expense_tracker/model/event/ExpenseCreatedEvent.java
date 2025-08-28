package com.example.expense_tracker.model.event;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;


public record ExpenseCreatedEvent(
        Long expenseId,
        String userEmail,
        BigDecimal amount,
        String currencyCode,
        String month) {

}
