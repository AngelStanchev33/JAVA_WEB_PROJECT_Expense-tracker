package com.example.expense_tracker.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "budgets", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "month"})
})
@Getter
@Setter
public class BudgetEntity extends BaseEntity{

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @NotNull
    @Column(name = "month", nullable = false, length = 7)
    private String month;

    @NotNull
    @Positive
    @Column(name = "budget_limit", nullable = false, precision = 19, scale = 2)
    private BigDecimal budgetLimit;

    @NotNull
    @PositiveOrZero
    @Column(name = "spent", nullable = false, precision = 19, scale = 2)
    private BigDecimal spent = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "currency_id")
    private CurrencyEntity currency;

}
