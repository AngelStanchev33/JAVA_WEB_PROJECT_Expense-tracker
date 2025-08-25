package com.example.expense_tracker.model.dto;

import com.example.expense_tracker.model.entity.CurrencyEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.security.core.parameters.P;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Accessors(chain = true)
public class ExpenseResponseDto {
    private Long id;
    private String description;
    private BigDecimal amount;
    private String category;
    private LocalDate date;
    private String user;
    private CurrencyEntity currency;
}