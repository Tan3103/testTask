package com.example.testTask.controllers;

import com.example.testTask.models.CurrencyExchangeRate;
import com.example.testTask.models.Expense;
import com.example.testTask.models.MonthlyLimit;
import com.example.testTask.repositories.CurrencyExchangeRateRepository;
import com.example.testTask.repositories.ExpenseRepository;
import com.example.testTask.repositories.MonthlyLimitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    private final ExpenseRepository expenseRepository;
    private final MonthlyLimitRepository monthlyLimitRepository;
    private final CurrencyExchangeRateRepository currencyExchangeRateRepository;

    @Autowired
    public ExpenseController(ExpenseRepository expenseRepository,
                             MonthlyLimitRepository monthlyLimitRepository,
                             CurrencyExchangeRateRepository currencyExchangeRateRepository) {
        this.expenseRepository = expenseRepository;
        this.monthlyLimitRepository = monthlyLimitRepository;
        this.currencyExchangeRateRepository = currencyExchangeRateRepository;
    }

    @PostMapping("/add")
    public void addExpense(@RequestBody Expense expense) {
        MonthlyLimit monthlyLimit = monthlyLimitRepository.findByCategoryAndEndDateAfter("goods", expense.getDate());
        if (monthlyLimit == null) {
            monthlyLimit = new MonthlyLimit("goods", 1000, new Date(), null);
        }

        double exchangeRateKZTToUSD = getExchangeRateFromDatabase("KZT", "USD", expense.getDate());
        double exchangeRateRUBToUSD = getExchangeRateFromDatabase("RUB", "USD", expense.getDate());

        double expenseInUSD = 0.0;

        if (exchangeRateKZTToUSD != 0.0) {
            expenseInUSD += expense.getAmount() / exchangeRateKZTToUSD;
        }

        if (exchangeRateRUBToUSD != 0.0) {
            expenseInUSD += expense.getAmount() / exchangeRateRUBToUSD;
        }

        if (expenseInUSD > monthlyLimit.getLimitAmount()) {
            expense.setLimitExceeded(true);
        }
        expenseRepository.save(expense);
    }

    @GetMapping("/limit-exceeded-transactions")
    public List<Expense> getLimitExceededTransactions() {
        // Получаем дату последнего установленного лимита
        Date lastLimitDate = getLastLimitDate();

        // Получаем актуальные курсы обмена из базы данных
        double exchangeRateKZTToUSD = getExchangeRateFromDatabase("KZT", "USD", new Date());
        double exchangeRateRUBToUSD = getExchangeRateFromDatabase("RUB", "USD", new Date());

        // Получаем все расходные операции, выполненные после последнего установленного лимита
        List<Expense> expenses = expenseRepository.findAllByDateAfter(lastLimitDate);

        for (Expense expense : expenses) {
            double expenseInUSD = 0.0;

            if (exchangeRateKZTToUSD != 0.0) {
                expenseInUSD += expense.getAmount() / exchangeRateKZTToUSD;
            }

            if (exchangeRateRUBToUSD != 0.0) {
                expenseInUSD += expense.getAmount() / exchangeRateRUBToUSD;
            }

            if (expenseInUSD > getMonthlyLimitAmount(expense.getDate())) {
                expense.setLimitExceeded(true);
            }
        }

        return expenses;
    }

    private double getExchangeRateFromDatabase(String fromCurrency, String toCurrency, Date date) {
        String currencyPair = fromCurrency + "/" + toCurrency;
        CurrencyExchangeRate exchangeRate = currencyExchangeRateRepository.findFirstByCurrencyPairAndDateOrderByDateDesc(currencyPair, date);
        if (exchangeRate != null) {
            return exchangeRate.getRate();
        }
        return 0.0;
    }

    private Date getLastLimitDate() {
        MonthlyLimit lastLimit = monthlyLimitRepository.findFirstByOrderByEndDateDesc();
        if (lastLimit != null) {
            return lastLimit.getEndDate();
        }
        return new Date(0);
    }

    private double getMonthlyLimitAmount(Date date) {
        List<Object[]> result = monthlyLimitRepository.findMonthlyLimitsForDate(date);
        double limitAmount = 0.0;
        for (Object[] row : result) {
            String category = (String) row[0];
            Double amount = (Double) row[1];
            if ("goods".equals(category)) {
                limitAmount += amount;
            }
        }
        if (limitAmount == 0.0) {
            return 1000;
        }
        return limitAmount;
    }
}

