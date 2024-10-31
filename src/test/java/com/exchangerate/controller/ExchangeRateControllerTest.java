package com.exchangerate.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.exchangerate.model.entities.ExchangeRate;
import com.exchangerate.services.ExchangeRateService;

public class ExchangeRateControllerTest {

    @InjectMocks
    private ExchangeRateController exchangeRateController;

    @Mock
    private ExchangeRateService exchangeRateService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return the latest exchange rate when it exists")
    void getLatestRateReturnsLatestRateWhenExists() {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setRate(5.12);
        exchangeRate.setBaseCurrency("USD"); // Add base currency for better testing
        exchangeRate.setTargetCurrency("BRL"); // Add target currency for better testing
        exchangeRate.setTimestamp(LocalDate.now().atStartOfDay()); // Set timestamp for better testing
        exchangeRate.setApiTimestamp(LocalDate.now().atStartOfDay()); // Set API timestamp for better testing

        when(exchangeRateService.getLatestRate()).thenReturn(exchangeRate);

        ResponseEntity<ExchangeRate> response = exchangeRateController.getLatestRate();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(exchangeRate, response.getBody());
    }

    @Test
    @DisplayName("Should return 404 Not Found when no latest exchange rate exists")
    void getLatestRateReturnsNotFoundWhenNoRateExists() {
        when(exchangeRateService.getLatestRate()).thenReturn(null);

        ResponseEntity<ExchangeRate> response = exchangeRateController.getLatestRate();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return historical rates when data exists within the specified date range")
    void getHistoricalRatesReturnsRatesWhenExists() {
        LocalDate startDate = LocalDate.of(2024, 10, 1);
        LocalDate endDate = LocalDate.of(2024, 10, 31);
        List<ExchangeRate> expectedRates = new ArrayList<>();
        
        ExchangeRate rate1 = new ExchangeRate();
        rate1.setRate(5.10);
        rate1.setBaseCurrency("USD");
        rate1.setTargetCurrency("BRL");
        expectedRates.add(rate1);

        ExchangeRate rate2 = new ExchangeRate();
        rate2.setRate(5.20);
        rate2.setBaseCurrency("USD");
        rate2.setTargetCurrency("BRL");
        expectedRates.add(rate2);

        when(exchangeRateService.getHistoricalRates(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)))
            .thenReturn(expectedRates);

        List<ExchangeRate> actualRates = exchangeRateController.getHistoricalRates(startDate, endDate);

        assertEquals(expectedRates.size(), actualRates.size());
        assertEquals(expectedRates, actualRates);
    }

    @Test
    @DisplayName("Should return an empty list when no historical rates exist within the specified date range")
    void getHistoricalRatesReturnsEmptyListWhenNoRatesExist() {
        LocalDate startDate = LocalDate.of(2024, 10, 1);
        LocalDate endDate = LocalDate.of(2024, 10, 31);

        when(exchangeRateService.getHistoricalRates(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)))
                .thenReturn(new ArrayList<>());

        List<ExchangeRate> actualRates = exchangeRateController.getHistoricalRates(startDate, endDate);

        assertEquals(0, actualRates.size());
    }
    
    @Test
    @DisplayName("Should call fetchAndSaveExchangeRate method and return 200 OK")
    void fetchAndSaveCallsServiceAndReturnsOk() {
        ResponseEntity<Void> response = exchangeRateController.fetchAndSave();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(exchangeRateService).fetchAndSaveExchangeRate();
    }
}
