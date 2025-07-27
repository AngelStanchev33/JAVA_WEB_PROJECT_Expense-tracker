package com.example.expense_tracker.model.dto;

import com.example.expense_tracker.model.validation.FieldMatch;
import com.example.expense_tracker.model.validation.UniqueEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@FieldMatch(first = "password", second = "confirmPassword", message = "Passwords do not match")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {

    @Email
    @NotBlank
    @UniqueEmail
    private String email;

    @NotBlank
    @Size(min = 3, max = 20)
    private String firstname;

    @NotBlank
    @Size(min = 3, max = 20)
    private String lastname;

    @NotBlank
    @Size(min = 8)
    private String password;

    @NotBlank
    @Size(min = 8)
    private String confirmPassword;

    @NotEmpty
    private List<UserRoleDto> userRoles;
}
