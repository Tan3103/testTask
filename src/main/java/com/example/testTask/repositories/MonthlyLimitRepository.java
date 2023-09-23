package com.example.testTask.repositories;

import com.example.testTask.models.MonthlyLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MonthlyLimitRepository extends JpaRepository<MonthlyLimit, Long> {
    MonthlyLimit findByCategoryAndEndDateAfter(String category, Date date);

    MonthlyLimit findFirstByOrderByEndDateDesc();

    @Query("SELECT ml.category, ml.limitAmount " +
            "FROM MonthlyLimit ml " +
            "WHERE ml.startDate <= :date AND (ml.endDate IS NULL OR ml.endDate >= :date)")
    List<Object[]> findMonthlyLimitsForDate(@Param("date") Date date);
}
