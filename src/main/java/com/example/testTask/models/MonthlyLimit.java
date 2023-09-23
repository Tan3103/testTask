package com.example.testTask.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MonthlyLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String category;
    private double limitAmount;
    private Date startDate;
    private Date endDate;

    public MonthlyLimit(String category, double limitAmount, Date startDate, Date endDate) {
        this.category = category;
        this.limitAmount = limitAmount;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
