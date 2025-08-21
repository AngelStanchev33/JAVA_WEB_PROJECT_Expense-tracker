package com.example.expense_tracker.init;

import com.example.expense_tracker.service.ExRateService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(0)
@Component
@RequiredArgsConstructor
public class ExchangeRateInitializer implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeRateInitializer.class);

    private final ExRateService exRateService;

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("=== Exchange Rate Initializer Started ===");
        
        if (!exRateService.hasInitializedExRates()) {
            LOGGER.info("No exchange rates found. Fetching from external API...");
            try {
                var exRatesDTO = exRateService.fetchExRates();
                LOGGER.info("Fetched {} exchange rates from external API", exRatesDTO.rates().size());
                
                exRateService.updateRates(exRatesDTO);
                LOGGER.info("Successfully initialized exchange rates in database");
            } catch (Exception e) {
                LOGGER.error("Failed to initialize exchange rates: {}", e.getMessage(), e);
                throw e;
            }
        } else {
            LOGGER.info("Exchange rates already initialized. Skipping fetch.");
        }
        
        LOGGER.info("=== Exchange Rate Initializer Completed ===");
    }
}