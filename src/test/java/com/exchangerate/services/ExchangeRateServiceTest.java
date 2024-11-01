package com.exchangerate.services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exchangerate.model.entities.ExchangeRate;
import com.exchangerate.model.repositories.ExchangeRateRepository;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceTest {

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @Mock
    private ExchangeRateApiClient exchangeRateApiClient;

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return the latest exchange rate")
    void testGetLatestRate() {
        ExchangeRate mockRate = new ExchangeRate();
        mockRate.setBaseCurrency("USD");
        mockRate.setTargetCurrency("BRL");
        mockRate.setRate(5.25);
        mockRate.setTimestamp(LocalDateTime.now());

        when(exchangeRateRepository.findTopByOrderByTimestampDesc()).thenReturn(mockRate);

        ExchangeRate latestRate = exchangeRateService.getLatestRate();

        assertEquals(mockRate, latestRate);
        assertEquals("USD", latestRate.getBaseCurrency());
        assertEquals("BRL", latestRate.getTargetCurrency());
        assertEquals(5.25, latestRate.getRate());
    }

    @Test
    @DisplayName("Should return null when no exchange rates exist in the database")
    void testGetLatestRateWhenNoRatesExist() {
        when(exchangeRateRepository.findTopByOrderByTimestampDesc()).thenReturn(null);
        ExchangeRate latestRate = exchangeRateService.getLatestRate();
        assertEquals(null, latestRate);
    }

    @Test
    @DisplayName("Should return the latest rate even if timestamp is in the future")
    void testGetLatestRateWithFutureTimestamp() {
        ExchangeRate futureRate = new ExchangeRate("USD", "BRL", 5.5, LocalDateTime.now().plusDays(1),
                LocalDateTime.now());
        when(exchangeRateRepository.findTopByOrderByTimestampDesc()).thenReturn(futureRate);
        ExchangeRate latestRate = exchangeRateService.getLatestRate();
        assertEquals(futureRate, latestRate);
    }

    @Test
    @DisplayName("Should return the latest rate with correct properties")
    void testGetLatestRateProperties() {
        ExchangeRate mockRate = new ExchangeRate("USD", "BRL", 5.25, LocalDateTime.now(), LocalDateTime.now());
        when(exchangeRateRepository.findTopByOrderByTimestampDesc()).thenReturn(mockRate);
        ExchangeRate latestRate = exchangeRateService.getLatestRate();
        assertEquals("USD", latestRate.getBaseCurrency());
        assertEquals("BRL", latestRate.getTargetCurrency());
        assertEquals(5.25, latestRate.getRate());
    }

    @Test
    @DisplayName("Should throw exception when repository fails")
    void testGetLatestRateWithRepositoryException() {
        when(exchangeRateRepository.findTopByOrderByTimestampDesc()).thenThrow(new RuntimeException("Database error"));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> exchangeRateService.getLatestRate());
        assertEquals("Database error", exception.getMessage());
    }

    @Test
    @DisplayName("Should return historical rates for a valid interval")
    void testGetHistoricalRatesWithValidInterval() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(5);
        LocalDateTime endDate = LocalDateTime.now();
        ExchangeRate rate1 = new ExchangeRate("USD", "BRL", 5.25, startDate.plusDays(1), LocalDateTime.now());
        ExchangeRate rate2 = new ExchangeRate("USD", "BRL", 5.35, startDate.plusDays(2), LocalDateTime.now());

        when(exchangeRateRepository.findAllByTimestampBetween(startDate, endDate)).thenReturn(List.of(rate1, rate2));

        List<ExchangeRate> rates = exchangeRateService.getHistoricalRates(startDate, endDate);

        assertEquals(2, rates.size());
        assertEquals(rate1, rates.get(0));
        assertEquals(rate2, rates.get(1));
    }

    @Test
    @DisplayName("Should return an empty list when no data in interval")
    void testGetHistoricalRatesWithNoDataInInterval() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(10);
        LocalDateTime endDate = LocalDateTime.now().minusDays(5);

        when(exchangeRateRepository.findAllByTimestampBetween(startDate, endDate)).thenReturn(List.of());

        List<ExchangeRate> rates = exchangeRateService.getHistoricalRates(startDate, endDate);

        assertTrue(rates.isEmpty());
    }

    @Test
    @DisplayName("Should return an empty list when dates are inverted")
    void testGetHistoricalRatesWithInvertedDates() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().minusDays(5);

        when(exchangeRateRepository.findAllByTimestampBetween(startDate, endDate)).thenReturn(List.of());

        List<ExchangeRate> rates = exchangeRateService.getHistoricalRates(startDate, endDate);

        assertTrue(rates.isEmpty());
    }

    @Test
    @DisplayName("Should throw exception when repository fails for historical rates")
    void testGetHistoricalRatesWithRepositoryException() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(5);
        LocalDateTime endDate = LocalDateTime.now();

        when(exchangeRateRepository.findAllByTimestampBetween(startDate, endDate))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> exchangeRateService.getHistoricalRates(startDate, endDate));
        assertEquals("Database error", exception.getMessage());
    }

    @Test
    @DisplayName("Should fetch and save exchange rate successfully")
    void fetchAndSaveExchangeRate_Success() throws IOException, InterruptedException {
        ExchangeRate mockRate = new ExchangeRate("USD", "BRL", 5.25, LocalDateTime.now(), LocalDateTime.now());

        when(exchangeRateApiClient.fetchExchangeRate()).thenReturn(mockRate);

        exchangeRateService.fetchAndSaveExchangeRate();

        ArgumentCaptor<ExchangeRate> exchangeRateCaptor = ArgumentCaptor.forClass(ExchangeRate.class);
        verify(exchangeRateRepository, times(1)).save(exchangeRateCaptor.capture());

        ExchangeRate savedRate = exchangeRateCaptor.getValue();

        assertEquals(5.25, savedRate.getRate());
        assertEquals("USD", savedRate.getBaseCurrency());
        assertEquals("BRL", savedRate.getTargetCurrency());
        assertEquals(mockRate.getApiTimestamp().getMinute(), savedRate.getApiTimestamp().getMinute());
    }

    @Test
    @DisplayName("Should handle IOException when fetching data")
    void fetchAndSaveExchangeRate_Error() throws IOException, InterruptedException {
        when(exchangeRateApiClient.fetchExchangeRate()).thenThrow(new IOException("Network error"));

        exchangeRateService.fetchAndSaveExchangeRate();

        verify(exchangeRateRepository, never()).save(any());
    }

}
