package com.exchangerate.model.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exchangerate.model.entities.ExchangeRate;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    List<ExchangeRate> findAllByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);
    ExchangeRate findTopByOrderByTimestampDesc();
}
