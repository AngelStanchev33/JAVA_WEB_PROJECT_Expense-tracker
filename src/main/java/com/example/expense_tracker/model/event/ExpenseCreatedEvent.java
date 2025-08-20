package com.example.expense_tracker.model.event;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Accessors(chain = true)
public class ExpenseCreatedEvent {

    private final Long expenseId;
    private final String userEmail;
    private final BigDecimal amount;
    private final String month;


    public ExpenseCreatedEvent(Long expenseId, String userEmail, BigDecimal amount, String month) {
        this.expenseId = expenseId;
        this.userEmail = userEmail;
        this.amount = amount;
        this.month = month;
    }
}
