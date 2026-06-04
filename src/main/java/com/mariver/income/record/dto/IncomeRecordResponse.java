package com.mariver.income.record.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record IncomeRecordResponse(Long id, Long incomeSourceId, Integer recordMonth,
                                   Integer recordYear, BigDecimal expectedAmount, BigDecimal receivedAmount,
                                   boolean received, LocalDateTime receivedAt, String note)
{

}
