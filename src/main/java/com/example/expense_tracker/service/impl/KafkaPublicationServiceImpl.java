package com.example.expense_tracker.service.impl;

import com.example.expense_tracker.config.KafkaConfig;
import com.example.expense_tracker.model.dto.ExRateDTO;
import com.example.expense_tracker.service.KafkaPublicationService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaPublicationServiceImpl implements KafkaPublicationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaPublicationServiceImpl.class);

    private final KafkaTemplate<String, ExRateDTO> kafkaTemplate;

    public KafkaPublicationServiceImpl(KafkaTemplate<String, ExRateDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishExRate(ExRateDTO exRateDTO) {
        kafkaTemplate
            .send(KafkaConfig.EX_RATES_TOPIC, exRateDTO.currency(), exRateDTO)
            .whenComplete((res, ex) -> {
                if (ex == null) {
                    RecordMetadata metadata = res.getRecordMetadata();
                    LOGGER.info("Successfully sent key {} to topic/partition/offset={}/{}/{}",
                        exRateDTO.currency(),
                        metadata.topic(),
                        metadata.partition(), 
                        metadata.offset()
                    );
                } else {
                    LOGGER.error("Error producing message with key {}", 
                        exRateDTO.currency(), ex);
                }
            });
    }
}