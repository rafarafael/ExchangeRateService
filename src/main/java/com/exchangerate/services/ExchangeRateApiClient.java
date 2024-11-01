package com.exchangerate.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.exchangerate.model.entities.ExchangeRate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ExchangeRateApiClient {

    @Value("${api.exchange-rate.url}")
    private String apiUrl;

    @Value("${api.exchange-rate.key}")
    private String apiKey;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ExchangeRateApiClient(HttpClient httpClient) {
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public ExchangeRate fetchExchangeRate() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("x-rapidapi-key", apiKey)
                .header("x-rapidapi-host", "exchange-rate-api1.p.rapidapi.com")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        JsonNode jsonNode = objectMapper.readTree(response.body());
        JsonNode rates = jsonNode.get("rates");
        String base = jsonNode.get("base").asText();
        String timeUtc = jsonNode.get("time_update").get("time_utc").asText();
        OffsetDateTime apiTimestamp = OffsetDateTime.parse(timeUtc);
        double rateValue = rates.get("BRL").asDouble();

        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setBaseCurrency(base);
        exchangeRate.setTargetCurrency("BRL");
        exchangeRate.setRate(rateValue);
        exchangeRate.setTimestamp(LocalDateTime.now());
        exchangeRate.setApiTimestamp(apiTimestamp.toLocalDateTime());

        return exchangeRate;
    }
}
