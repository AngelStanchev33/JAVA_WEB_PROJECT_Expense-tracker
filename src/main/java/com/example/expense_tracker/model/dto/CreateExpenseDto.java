package com.example.expense_tracker.model.dto;

import com.example.expense_tracker.model.enums.CategoryEnum;
import com.example.expense_tracker.model.validation.ValidEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Accessors(chain = true)
public class CreateExpenseDto {
    
    @Schema(description = "Expense description", example = "Lunch with client")
    @NotBlank(message = "Description is required")
    private String description;

    @Schema(description = "Expense amount", example = "24.50")
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @Schema(description = "Expense category", example = "FOOD")
    @NotBlank(message = "Category is required")
    @ValidEnum(enumClass = CategoryEnum.class, message = "Invalid category. Valid values: FOOD, BILLS, TRANSPORT, ENTERTAINMENT, OTHER")
    private String category;
    
    @Schema(description = "Expense date", example = "2025-08-25")
    @NotNull(message = "Date is required")
    @FutureOrPresent(message = "Date cannot be in the past")
    private LocalDate expenseDate;;

    @Schema(description = "Currency information", example = "EUR")
    @NotBlank(message = "Currency is required")
    private String currencyCode;
}