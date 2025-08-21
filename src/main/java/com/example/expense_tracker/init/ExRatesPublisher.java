package com.example.expense_tracker.init;

import com.example.expense_tracker.service.ExRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExRatesPublisher implements CommandLineRunner {
    
    private final ExRateService exRateService;

    @Override
    public void run(String... args) throws Exception {
//        log.info("=== Publishing Exchange Rates to Kafka ===");
//        exRateService.publishExRates();
//        log.info("=== Exchange Rates Published Successfully ===");
    }
}