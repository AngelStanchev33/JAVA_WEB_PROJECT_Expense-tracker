package com.example.expense_tracker.service;

import com.example.expense_tracker.model.dto.RegisterRequestDto;

public interface UserService {

    public void register(RegisterRequestDto requestDto);
}
