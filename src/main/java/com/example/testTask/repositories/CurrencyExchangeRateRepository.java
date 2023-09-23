package com.example.testTask.repositories;

import com.example.testTask.models.CurrencyExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface CurrencyExchangeRateRepository extends JpaRepository<CurrencyExchangeRate, Long> {
    CurrencyExchangeRate findFirstByCurrencyPairAndDateOrderByDateDesc(String currencyPair, Date date);
}
