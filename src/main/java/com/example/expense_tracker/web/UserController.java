package com.example.expense_tracker.web;

import com.example.expense_tracker.model.dto.CurrencyUpdateDto;
import com.example.expense_tracker.service.UserPreferenceService;
import com.example.expense_tracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("user")
public class UserController {

    private final UserPreferenceService userPreferenceService;

    public UserController(UserPreferenceService userPreferenceService) {
        this.userPreferenceService = userPreferenceService;
    }

    @PutMapping("/currency")
    public ResponseEntity<?> updateCurrency(@RequestBody CurrencyUpdateDto currencyUpdateDto, Principal principal) {
        userPreferenceService.updateCurrency(principal.getName(), currencyUpdateDto);

        return ResponseEntity.ok("updated currency preference");
    }
}
