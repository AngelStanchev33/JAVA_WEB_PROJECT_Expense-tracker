package com.example.expense_tracker.model.dto;

import java.math.BigDecimal;

public record ExRateDTO(String currency, BigDecimal rate) {
}