package com.mariver.bill.dto;

import com.mariver.bill.BillCategory;
import com.mariver.bill.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record BillRecordResponse(Long id, String billName, BillCategory category,
                                 BigDecimal estimatedAmount, BigDecimal actualAmount,
                                 LocalDate dueDate, boolean paid, LocalDate paidDate, PaymentType paymentType,
                                 boolean autoPay) {
}
