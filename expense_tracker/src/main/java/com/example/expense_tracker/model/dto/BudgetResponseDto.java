package com.example.expense_tracker.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class BudgetResponseDto {

    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private YearMonth month;
    private BigDecimal limit;
    private BigDecimal spent;
    private String userEmail;
    
    // Calculated fields for frontend
    private BigDecimal remaining;
    private Double spentPercentage;
}
