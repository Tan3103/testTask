package com.example.testTask.repositories;

import com.example.testTask.models.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByLimitExceededTrue();

    List<Expense> findAllByDateAfter(Date date);

    @Query("SELECT e " +
            "FROM Expense e " +
            "WHERE e.date > :date AND e.limitExceeded = true")
    List<Expense> findLimitExceededTransactionsSinceDate(@Param("date") Date date);
}
