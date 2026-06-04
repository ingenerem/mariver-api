package com.mariver.bill.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BillRecordRequest(Long billId, Long billScheduleId, Integer recordMonth,
                                Integer recordYear, BigDecimal actualAmount, LocalDateTime paidAt) {
}
