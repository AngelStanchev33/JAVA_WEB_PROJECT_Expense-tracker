package com.example.expense_tracker.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "forex.api")
@Getter
@Setter
public class ForexApiConfig {
    
    private String key;     // API key for external service
    private String url;     // API endpoint URL
    private String base;    // Base currency (must be USD for free API)

    @PostConstruct
    public void checkConfiguration() {
        verifyNotNullOrEmpty("key", key);
        verifyNotNullOrEmpty("base", base);
        verifyNotNullOrEmpty("url", url);

        if (!"USD".equals(base)) {
            throw new IllegalStateException("Free API only supports USD as base currency");
        }
    }

    private void verifyNotNullOrEmpty(String paramName, String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException("Parameter '" + paramName + "' cannot be null or empty");
        }
    }
}