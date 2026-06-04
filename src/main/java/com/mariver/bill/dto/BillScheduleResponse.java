package com.mariver.bill.dto;

import com.mariver.bill.IntervalUnit;

public record BillScheduleResponse(
        Long id,
        Integer intervalValue,
        IntervalUnit intervalUnit,
        Integer dueDay,
        Integer dueMonth) {
}