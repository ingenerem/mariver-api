package com.mariver.bill.dto;

import com.mariver.bill.BillCategory;

import java.math.BigDecimal;

public record BillResponse(
        Long id,
        String name,
        BigDecimal amount,
        BillCategory category,
        boolean active,
        BillScheduleResponse schedule
) {
}