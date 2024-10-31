package com.exchangerate.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.exchangerate.model.entities.ExchangeRate;
import com.exchangerate.services.ExchangeRateService;

@RestController
@RequestMapping("/exchange")
public class ExchangeRateController {
    @Autowired
    private ExchangeRateService exchangeRateService;

    @GetMapping("/latest")
    public ResponseEntity<ExchangeRate> getLatestRate() {
        ExchangeRate latestRate = exchangeRateService.getLatestRate();
        return latestRate != null ? ResponseEntity.ok(latestRate) : ResponseEntity.notFound().build();
    }

    
    @GetMapping("/historical")
    public List<ExchangeRate> getHistoricalRates(
        @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate  startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        return exchangeRateService.getHistoricalRates(startDateTime, endDateTime);
    }
        
        @PostMapping("/fetch")
        public ResponseEntity<Void> fetchAndSave() {
            exchangeRateService.fetchAndSaveExchangeRate();
            return ResponseEntity.ok().build();
        }

}
