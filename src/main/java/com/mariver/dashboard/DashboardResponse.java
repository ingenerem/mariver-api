package com.mariver.dashboard;

import java.math.BigDecimal;

public record DashboardResponse(BigDecimal currentBalance, BigDecimal protectedBills,
                                BigDecimal emergencyFund, BigDecimal otherSavings,
                                BigDecimal spendableAmount) {
}