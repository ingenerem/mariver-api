package com.mariver.income.record.dto;

import java.math.BigDecimal;

public record IncomeRecordRequest(BigDecimal receivedAmount, String note) {
}
