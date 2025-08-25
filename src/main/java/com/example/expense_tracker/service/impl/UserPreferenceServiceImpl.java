package com.example.expense_tracker.service.impl;

import com.example.expense_tracker.exception.UserNotFoundException;
import com.example.expense_tracker.model.dto.CurrencyUpdateDto;
import com.example.expense_tracker.model.entity.CurrencyEntity;
import com.example.expense_tracker.model.entity.UserPreferenceEntity;
import com.example.expense_tracker.repository.CurrencyRepository;
import com.example.expense_tracker.repository.UserPreferenceRepository;
import com.example.expense_tracker.service.UserPreferenceService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserPreferenceServiceImpl implements UserPreferenceService {
    private final UserPreferenceRepository userPreferenceRepository;
    private final CurrencyRepository currencyRepository;

    public UserPreferenceServiceImpl(UserPreferenceRepository userPreferenceRepository, CurrencyRepository currencyRepository) {
        this.userPreferenceRepository = userPreferenceRepository;
        this.currencyRepository = currencyRepository;
    }

    @Override
    public void updateCurrency(String userEmail, CurrencyUpdateDto currencyUpdateDto) {
        UserPreferenceEntity userPreference = userPreferenceRepository
                .findByUserEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User preferences not found"));

        CurrencyEntity currency = currencyRepository
                .findByCode(currencyUpdateDto.code())
                .orElseThrow(() -> new RuntimeException("Currency not found"));

        userPreference.setDefaultCurrency(currency);
        userPreferenceRepository.save(userPreference); // ← ВАЖНО!

    }
}
