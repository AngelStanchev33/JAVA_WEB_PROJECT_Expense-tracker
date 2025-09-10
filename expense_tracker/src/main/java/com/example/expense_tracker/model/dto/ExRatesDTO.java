package com.example.expense_tracker.model.dto;

import java.math.BigDecimal;
import java.util.Map;

public record ExRatesDTO(String base, Map<String, BigDecimal> rates) {
}