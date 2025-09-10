package com.example.expense_tracker.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter
@Setter
@Accessors(chain = true)
public class CreateBudgetDto {

    @Schema(description = "Month in YYYY-MM format", example = "2025-08")
    @NotBlank(message = "Month cannot be blank")
    private String month;

    @Schema(description = "Budget limit amount", example = "1000.50")
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal budgetLimit;

    @Schema(description = "Currency information", example = "EUR")
    @NotBlank(message = "Currency cannot be blank")
    private String currencyCode;

}
