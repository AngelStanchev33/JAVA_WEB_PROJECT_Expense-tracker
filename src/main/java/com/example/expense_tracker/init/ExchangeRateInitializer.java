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

        try {
            var exRatesDTO = exRateService.fetchExRates();
            LOGGER.info("Fetched {} rates from external API", exRatesDTO.rates().size());

            exRateService.updateRates(exRatesDTO); // Винаги update
            LOGGER.info("Successfully updated exchange rates");
        } catch (Exception e) {
            LOGGER.error("Failed to update exchange rates: {}", e.getMessage(), e);
            throw e;
        }

        LOGGER.info("=== Exchange Rate Initializer Completed ===");
    }

}