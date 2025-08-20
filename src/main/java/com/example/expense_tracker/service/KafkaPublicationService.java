package com.example.expense_tracker.service;

import com.example.expense_tracker.model.dto.ExRateDTO;

public interface KafkaPublicationService {
    
    void publishExRate(ExRateDTO exRateDTO);
}