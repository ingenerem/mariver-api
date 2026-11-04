package com.mariver.dashboard;

import java.math.BigDecimal;

public record DashboardResponse(BigDecimal currentBalance, BigDecimal protectedBills,
    BigDecimal accountBalance, BigDecimal spendableAmount) {
}