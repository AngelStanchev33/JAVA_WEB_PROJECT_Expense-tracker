package com.example.expense_tracker.model.dto;

import com.fasterxml.jackson.core.Base64Variant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "Data Transfer Object for updating expense information")
public class UpdateExpenseDto {
    
    @Schema(description = "Expense description", example = "Coffee at Starbucks")
    private String description;
    
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Schema(description = "Expense amount", example = "15.50")
    private BigDecimal amount;
    
    @Schema(description = "Expense category", example = "FOOD")
    private String category;
    
    @Schema(description = "Expense date", example = "2024-03-15")
    private LocalDate date;

    @Schema(description = "Change currency to BGN or EUR", example = "BGN")
    private String currency;
}
