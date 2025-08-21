package com.example.expense_tracker.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "ex_rates")
@Getter
@Setter
public class ExRateEntity extends BaseEntity {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "currency_id", nullable = false)
    private CurrencyEntity currency;

    @Positive
    @NotNull
    @Column(name = "rate", nullable = false, precision = 19, scale = 4)
    private BigDecimal rate;
}
