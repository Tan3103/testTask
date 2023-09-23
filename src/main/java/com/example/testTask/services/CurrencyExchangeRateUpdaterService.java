package com.example.testTask.services;

import com.example.testTask.models.CurrencyExchangeRate;
import com.example.testTask.repositories.CurrencyExchangeRateRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Service
public class CurrencyExchangeRateUpdaterService {
    private final CurrencyExchangeRateRepository currencyExchangeRateRepository;

    public CurrencyExchangeRateUpdaterService(CurrencyExchangeRateRepository currencyExchangeRateRepository) {
        this.currencyExchangeRateRepository = currencyExchangeRateRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateCurrencyExchangeRates() {
        try {
            String apiUrl = "https://twelvedata.com";
            RestTemplate restTemplate = new RestTemplate();

            CurrencyExchangeRate exchangeRate = new CurrencyExchangeRate();
            exchangeRate.setCurrencyPair("KZT/USD");
            exchangeRate.setRate(400);
            exchangeRate.setDate(new Date());
            currencyExchangeRateRepository.save(exchangeRate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
