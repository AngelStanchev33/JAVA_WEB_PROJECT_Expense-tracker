package com.example.expense_tracker.model.event;

import java.math.BigDecimal;

public record BudgetCreatedEvent(
        Long budgetID

) {
}
