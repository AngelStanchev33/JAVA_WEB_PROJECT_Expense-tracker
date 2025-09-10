package com.example.expense_tracker.service;

import com.example.expense_tracker.model.dto.CurrencyUpdateDto;
import com.example.expense_tracker.model.dto.RegisterRequestDto;
import org.springframework.security.core.Authentication;

public interface UserService {

    public void register(RegisterRequestDto requestDto);

    void registerWithOauth2(String login, String email);

    public Authentication loginWithOAuth(String email);

}
