package com.example.expense_tracker.model.dto;

import com.example.expense_tracker.model.validation.FieldMatch;
import com.example.expense_tracker.model.validation.UniqueEmail;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@FieldMatch(first = "password", second = "confirmPassword", message = "Passwords do not match")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class RegisterRequestDto {

    @Email(message = "Please provide a valid email address")
    @NotBlank(message = "Email is required")
    @UniqueEmail
    @Schema(example = "test@gmail.com")
    private String email;

    @NotBlank(message = "First name is required")
    @Size(min = 3, max = 20, message = "First name must be between 3 and 20 characters")
    @Schema(example = "Test")
    private String firstname;

    @NotBlank(message = "Last name is required")
    @Size(min = 3, max = 20, message = "Last name must be between 3 and 20 characters")
    @Schema(example = "User")
    private String lastname;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Schema(example = "12345678")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    @Size(min = 8, message = "Password confirmation must be at least 8 characters long")
    @Schema(example = "12345678")
    private String confirmPassword;

}
