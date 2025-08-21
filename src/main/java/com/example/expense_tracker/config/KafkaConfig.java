package com.example.expense_tracker.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true", matchIfMissing = false)
public class KafkaConfig {
    
    public static final String EX_RATES_TOPIC = "ex-rates";

    @Bean
    public NewTopic exRatesTopic() {
        return TopicBuilder.name(EX_RATES_TOPIC)
                .partitions(2)        // Number of partitions for parallel processing
                .compact()            // Log compaction enabled
                .build();
    }
}