package com.mariver.common.utils;

import java.time.LocalDate;

public record TimeSnapshot(int year, int day, int month) {

    public static TimeSnapshot currentDate(){
        LocalDate today = LocalDate.now();

        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();
        int currentDay = today.getDayOfMonth();
        return new TimeSnapshot(currentYear, currentDay, currentMonth);
    }
}
