package com.example.expense_tracker.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "Data Transfer Object for updating budget information")
public class UpdateBudgetDto {

    @Schema(description = "Budget month in YYYY-MM format", example = "2024-03")
    private String month;

    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Schema(description = "Budget limit amount", example = "1500.00")
    private BigDecimal budgetLimit;

    @Schema(description = "Change currency to BGN or EUR", example = "BGN")
    private String currency;

}
