package com.example.expense_tracker.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.YearMonth;
@Getter
@Setter
@Accessors(chain = true)
public class ExpenseCreatedEvent {

    private final Long expenseId;
    private final String userEmail;
    private final BigDecimal amount;


    public ExpenseCreatedEvent(Long expenseId, String userEmail, BigDecimal amount) {
        this.expenseId = expenseId;
        this.userEmail = userEmail;
        this.amount = amount;

    }
}
