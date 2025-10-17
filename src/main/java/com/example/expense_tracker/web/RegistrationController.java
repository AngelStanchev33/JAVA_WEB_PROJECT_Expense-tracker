package com.example.expense_tracker.web;

import com.example.expense_tracker.model.dto.RegisterRequestDto;
import com.example.expense_tracker.service.EmailService;
import com.example.expense_tracker.service.UserService;
import com.example.expense_tracker.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequestDto requestDto) {
        userService.register(requestDto);
        return ResponseEntity.ok().build();
    }

}
