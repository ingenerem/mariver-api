package com.mariver.common.utils;

import java.time.LocalDate;

public record DateRange(LocalDate startDate, LocalDate endDate) {


    public static DateRange currentMonth() {
        LocalDate today = LocalDate.now();

        LocalDate startDate = today.withDayOfMonth(1);
        LocalDate endDate = startDate.plusMonths(1);

        return new DateRange(startDate, endDate);
    }
}
