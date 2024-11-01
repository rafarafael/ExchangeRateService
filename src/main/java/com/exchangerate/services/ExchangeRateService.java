package com.exchangerate.services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.exchangerate.model.entities.ExchangeRate;
import com.exchangerate.model.repositories.ExchangeRateRepository;

@Service
public class ExchangeRateService {

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    @Autowired
    private ExchangeRateApiClient exchangeRateApiClient;

    public ExchangeRate getLatestRate() {
        return exchangeRateRepository.findTopByOrderByTimestampDesc();
    }

    public List<ExchangeRate> getHistoricalRates(LocalDateTime startDate, LocalDateTime endDate) {
        return exchangeRateRepository.findAllByTimestampBetween(startDate, endDate);
    }

    @Scheduled(fixedRateString = "${fetch.interval}")
    public void fetchAndSaveExchangeRate() {
        try {
            ExchangeRate exchangeRate = exchangeRateApiClient.fetchExchangeRate();
            exchangeRateRepository.save(exchangeRate);
        } catch (IOException | InterruptedException e) {
            System.out.println("Error fetching exchange rate: " + e.getMessage());
        }
    }
}
