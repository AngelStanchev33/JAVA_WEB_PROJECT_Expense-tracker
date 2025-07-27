package com.example.expense_tracker.model.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthRequestDto(

        @NotBlank(message = "Username is required") String username,

        @NotBlank(message = "Password is required") String password) {
}