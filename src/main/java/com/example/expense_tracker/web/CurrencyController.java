package com.example.expense_tracker.web;

import com.example.expense_tracker.service.ExRateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/currencies")
public class CurrencyController {

    private final ExRateService exRateService;

    public CurrencyController(ExRateService exRateService) {
        this.exRateService = exRateService;
    }

    @GetMapping
    public ResponseEntity<List<String>> getAllCurrencies() {
        List<String> currencies = exRateService.allSupportedCurrencies();
        return ResponseEntity.ok(currencies);
    }
}