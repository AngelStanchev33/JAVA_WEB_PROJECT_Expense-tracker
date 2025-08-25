package com.example.expense_tracker.service;

import com.example.expense_tracker.model.dto.CurrencyUpdateDto;

public interface UserPreferenceService {

    void updateCurrency(String userEmail, CurrencyUpdateDto currencyUpdateDto);
}
