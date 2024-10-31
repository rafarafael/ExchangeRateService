package com.exchangerate.model.repositories; // ou com.exchangerate.service;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.exchangerate.model.entities.ExchangeRate;

@DataJpaTest
@ActiveProfiles("test")
public class ExchangeRateRepositoryTest {

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    @Test
    @DisplayName("Should return all exchange rates between specified timestamps")
    void findAllByTimestampBetweenSuccess() {
        ExchangeRate rate1 = new ExchangeRate("USD", "BRL", 5.00, LocalDateTime.now().minusDays(1),
                LocalDateTime.now());
        ExchangeRate rate2 = new ExchangeRate("USD", "BRL", 5.10, LocalDateTime.now(), LocalDateTime.now());
        exchangeRateRepository.save(rate1);
        exchangeRateRepository.save(rate2);

        List<ExchangeRate> rates = exchangeRateRepository.findAllByTimestampBetween(
                LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(1));

        assertThat(rates).hasSize(2);
    }
    
    @Test
    @DisplayName("Should return no results when there are no matching records")
    void findAllByTimestampBetweenNoResults() {
        List<ExchangeRate> rates = exchangeRateRepository.findAllByTimestampBetween(
            LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(5)
        );
        assertThat(rates).isEmpty();
    }

    @Test
    @DisplayName("Should include records that are exactly on the timestamp limits")
    void findAllByTimestampBetweenWithExactLimits() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();

        ExchangeRate rate = new ExchangeRate("USD", "BRL", 5.15, start, end);
        exchangeRateRepository.save(rate);

        List<ExchangeRate> rates = exchangeRateRepository.findAllByTimestampBetween(
            start.minusSeconds(1), end.plusSeconds(1)
        );

        assertThat(rates).hasSize(1).contains(rate);
    }

    @Test
    @DisplayName("Should return a single result when the timestamp range includes only one record")
    void findAllByTimestampBetweenSingleResult() {
        ExchangeRate rate = new ExchangeRate("USD", "BRL", 5.20, LocalDateTime.now().minusHours(1),
                LocalDateTime.now());
        exchangeRateRepository.save(rate);

        List<ExchangeRate> rates = exchangeRateRepository.findAllByTimestampBetween(
                LocalDateTime.now().minusHours(2), LocalDateTime.now());

        assertThat(rates).hasSize(1).contains(rate);
    }

    @Test
    @DisplayName("Should return the latest exchange rate when multiple rates exist")
    void findTopByOrderByTimestampDescSuccess() {
        ExchangeRate rate1 = new ExchangeRate("USD", "BRL", 5.00, LocalDateTime.now().minusDays(2), LocalDateTime.now());
        ExchangeRate rate2 = new ExchangeRate("USD", "BRL", 5.10, LocalDateTime.now().minusDays(1), LocalDateTime.now());
        ExchangeRate rate3 = new ExchangeRate("USD", "BRL", 5.20, LocalDateTime.now(), LocalDateTime.now()); // taxa mais recente

        exchangeRateRepository.save(rate1);
        exchangeRateRepository.save(rate2);
        exchangeRateRepository.save(rate3);

        ExchangeRate latestRate = exchangeRateRepository.findTopByOrderByTimestampDesc();

        assertThat(latestRate).isNotNull();
        assertThat(latestRate.getRate()).isEqualTo(5.20); // Verifique se o valor é o esperado
    }

    @Test
    @DisplayName("Should return null when no exchange rates exist")
    void findTopByOrderByTimestampDescWhenNoRatesExist() {
        ExchangeRate latestRate = exchangeRateRepository.findTopByOrderByTimestampDesc();

        assertThat(latestRate).isNull();
    }
}
