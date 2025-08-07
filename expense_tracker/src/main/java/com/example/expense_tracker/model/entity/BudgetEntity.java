package com.example.expense_tracker.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.YearMonth;

@Entity
@Table(name = "budgets")
@Getter
@Setter
public class BudgetEntity extends BaseEntity{

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @NotNull
    @Column(name = "month", nullable = false, length = 7, columnDefinition = "VARCHAR(7)")
    private YearMonth month;

    @NotNull
    @Positive
    @Column(name = "limit", nullable = false, precision = 19, scale = 2)
    private BigDecimal limit;

    @NotNull
    @PositiveOrZero
    @Column(name = "spent", nullable = false, precision = 19, scale = 2)
    private BigDecimal spent = BigDecimal.ZERO;

}
