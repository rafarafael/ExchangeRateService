package com.exchangerate.services;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exchangerate.model.entities.ExchangeRate;

@ExtendWith(MockitoExtension.class)
class ExchangeRateApiClientTest {

    private ExchangeRateApiClient exchangeRateApiClient;

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        exchangeRateApiClient = new ExchangeRateApiClient(httpClient);

        exchangeRateApiClient.setApiUrl("https://api.example.com/rates");
        exchangeRateApiClient.setApiKey("testApiKey");
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Should fetch exchange rate successfully from the API")
    void testFetchExchangeRate() throws IOException, InterruptedException {
        String jsonResponse = "{ \"base\": \"USD\", \"rates\": { \"BRL\": 5.20 }, \"time_update\": { \"time_utc\": \"2023-10-31T12:00:00Z\" } }";

        when(httpResponse.body()).thenReturn(jsonResponse);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

        ExchangeRate exchangeRate = exchangeRateApiClient.fetchExchangeRate();

        assertEquals("USD", exchangeRate.getBaseCurrency());
        assertEquals("BRL", exchangeRate.getTargetCurrency());
        assertEquals(5.20, exchangeRate.getRate());
        assertEquals(LocalDateTime.now().getDayOfYear(), exchangeRate.getTimestamp().getDayOfYear());
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Should throw IOException when API fails")
    void testFetchExchangeRateApiFailure() throws IOException, InterruptedException {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenThrow(new IOException("Network error"));

        IOException exception = assertThrows(IOException.class, () -> {
            exchangeRateApiClient.fetchExchangeRate();
        });

        assertEquals("Network error", exception.getMessage());
    }
}
