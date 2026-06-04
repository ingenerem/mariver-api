package com.mariver.bill.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BillRecordResponse(Long id, Long billId, Long billScheduleId,
                                 String billName, Integer recordMonth,
                                 Integer recordYear, BigDecimal actualAmount,
                                 LocalDateTime paidAt, LocalDateTime createdAt) {
}
