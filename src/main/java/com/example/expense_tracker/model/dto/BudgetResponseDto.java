package com.example.expense_tracker.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class BudgetResponseDto {

    private Long id;

    private String month;
    private BigDecimal budgetLimit;
    private BigDecimal spent;
    private String user;
    
    // Calculated fields for frontend
    private BigDecimal remaining;
    private Double spentPercentage;
}
