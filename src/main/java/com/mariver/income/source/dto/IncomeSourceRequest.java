package com.mariver.income.source.dto;

import com.mariver.income.IncomeCategory;
import com.mariver.income.source.IncomeFrequency;

import java.math.BigDecimal;

public record IncomeSourceRequest(
        IncomeCategory category,
        String description,
        BigDecimal amount,
        IncomeFrequency frequency,
        boolean active

) {
}