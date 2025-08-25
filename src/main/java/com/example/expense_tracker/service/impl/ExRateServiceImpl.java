package com.example.expense_tracker.service.impl;

import com.example.expense_tracker.config.ForexApiConfig;
import com.example.expense_tracker.model.dto.ExRateDTO;
import com.example.expense_tracker.model.dto.ExRatesDTO;
import com.example.expense_tracker.model.entity.CurrencyEntity;
import com.example.expense_tracker.model.entity.ExRateEntity;
import com.example.expense_tracker.repository.CurrencyRepository;
import com.example.expense_tracker.repository.ExRateRepository;
import com.example.expense_tracker.service.ExRateService;
import com.example.expense_tracker.service.KafkaPublicationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ExRateServiceImpl implements ExRateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExRateServiceImpl.class);

    private final ExRateRepository exRateRepository;
    private final CurrencyRepository currencyRepository;
    private final ForexApiConfig forexApiConfig;
    private final RestClient restClient;

//    @Autowired
//    private KafkaPublicationService kafkaPublicationService;

    public ExRateServiceImpl(ExRateRepository exRateRepository,
                             CurrencyRepository currencyRepository,
                             ForexApiConfig forexApiConfig,
                             RestClient restClient) {
        this.exRateRepository = exRateRepository;
        this.currencyRepository = currencyRepository;
        this.forexApiConfig = forexApiConfig;
        this.restClient = restClient;
    }

    @Override
    public List<String> allSupportedCurrencies() {
        return currencyRepository
                .findAll()
                .stream()
                .map(CurrencyEntity::getCode)
                .toList();
    }

    @Override
    public ExRatesDTO fetchExRates() {
        LOGGER.info("Fetching exchange rates from external API");
        return restClient
                .get()
                .uri(forexApiConfig.getUrl(), forexApiConfig.getKey())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(ExRatesDTO.class);
    }

    @Override
    public void updateRates(ExRatesDTO exRatesDTO) {
        LOGGER.info("Updating {} rates.", exRatesDTO.rates().size());

        if (!forexApiConfig.getBase().equals(exRatesDTO.base())) {
            throw new IllegalArgumentException("Exchange rates base currency mismatch");
        }

        exRatesDTO.rates().forEach((currencyCode, rate) -> {
            CurrencyEntity currency = currencyRepository.findByCode(currencyCode)
                    .orElseGet(() -> {
                        CurrencyEntity newCurrency = new CurrencyEntity();
                        newCurrency.setCode(currencyCode);
                        return currencyRepository.save(newCurrency);
                    });

            ExRateEntity exRateEntity = exRateRepository.findByCurrencyCode(currencyCode)
                    .orElseGet(() -> {
                        ExRateEntity newRate = new ExRateEntity();
                        newRate.setCurrency(currency);
                        newRate.setRate(rate);
                        return exRateRepository.save(newRate);
                    });

            exRateEntity.setRate(rate);
            exRateRepository.save(exRateEntity);
        });
    }

    @Override
    public BigDecimal convert(String from, String to, BigDecimal amount) {
        return findExRate(from, to)
                .orElseThrow(() -> new RuntimeException("Conversion from " + from + " to " + to + " not possible!"))
                .multiply(amount);
    }

    private Optional<BigDecimal> findExRate(String from, String to) {
        if (Objects.equals(from, to)) {
            return Optional.of(BigDecimal.ONE);  // Same currency = 1:1 rate
        }

        // Get rates for both currencies relative to base currency (USD)
        Optional<BigDecimal> fromOpt = forexApiConfig.getBase().equals(from) ?
                Optional.of(BigDecimal.ONE) :  // Base currency has rate of 1
                exRateRepository.findByCurrencyCode(from).map(ExRateEntity::getRate);

        Optional<BigDecimal> toOpt = forexApiConfig.getBase().equals(to) ?
                Optional.of(BigDecimal.ONE) :
                exRateRepository.findByCurrencyCode(to).map(ExRateEntity::getRate);

        if (fromOpt.isEmpty() || toOpt.isEmpty()) {
            return Optional.empty();
        } else {
            // Cross-rate calculation: toRate / fromRate
            return Optional.of(toOpt.get().divide(fromOpt.get(), 2, RoundingMode.HALF_DOWN));
        }
    }

//    @Override
//    public void publishExRates() {
//        List<ExRateDTO> exRates = exRateRepository
//                .findAll()
//                .stream()
//                .sorted(Comparator.comparing(entity -> entity.getCurrency().getCode()))
//                .map(this::mapToExRateDTO)
//                .toList();
//
//        exRates.forEach(kafkaPublicationService::publishExRate);
//    }

    private ExRateDTO mapToExRateDTO(ExRateEntity entity) {
        return new ExRateDTO(entity.getCurrency().getCode(), entity.getRate());
    }
}
