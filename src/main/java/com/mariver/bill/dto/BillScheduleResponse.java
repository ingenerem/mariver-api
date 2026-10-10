package com.mariver.bill.dto;

import com.mariver.bill.IntervalUnit;

import java.time.LocalDate;

public record BillScheduleResponse( Long id, boolean active, Integer intervalValue,
                                    IntervalUnit intervalUnit, LocalDate startDate,
                                    boolean autoPay, LocalDate nextDueDate)
{ }