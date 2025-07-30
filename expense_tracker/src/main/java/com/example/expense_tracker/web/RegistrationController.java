package com.example.expense_tracker.web;

import com.example.expense_tracker.model.dto.RegisterRequestDto;
import com.example.expense_tracker.service.EmailService;
import com.example.expense_tracker.service.UserService;
import com.example.expense_tracker.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {

    private final UserService userService;
    private final EmailService emailService;

    public RegistrationController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto requestDto) {
        userService.register(requestDto);

//        emailService.sendRegistrationEmail(requestDto.getEmail(), requestDto.getFirstname());



        return ResponseEntity.ok("registration  successful");
    }

}
