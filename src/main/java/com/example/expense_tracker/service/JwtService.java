package com.example.expense_tracker.service;

import java.util.Map;

public interface JwtService {

    public String generateToken(String email, Map<String, Object> claims);
    public boolean isTokenValid(String token);
    public String extractEmail(String token);
}
