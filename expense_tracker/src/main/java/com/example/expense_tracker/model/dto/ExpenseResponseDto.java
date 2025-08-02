package com.example.expense_tracker.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

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
    private String userEmail;
}