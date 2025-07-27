package com.example.expense_tracker.web;

import com.example.expense_tracker.model.dto.AuthRequestDto;
import com.example.expense_tracker.model.dto.AuthResponseDto;
import com.example.expense_tracker.model.user.ExpenseTrackerUserDetails;
import com.example.expense_tracker.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDto request) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        ExpenseTrackerUserDetails user = (ExpenseTrackerUserDetails) authenticate.getPrincipal();

        List<String> roles = user.getAuthorities().stream().map(Objects::toString).toList();

        Map<String, Object> claims = Map.of("roles", roles);

        String token = jwtService.generateToken(request.username(), claims);

        return ResponseEntity.ok(new AuthResponseDto(token));

    }
}
