package com.example.expense_tracker.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AuthRequestDto(

        @Schema(description = "User email address", example = "test@gmail.com")
        @NotBlank(message = "Username is required") String username,

        @Schema(description = "User password", example = "12345678")
        @NotBlank(message = "Password is required") String password) {
}