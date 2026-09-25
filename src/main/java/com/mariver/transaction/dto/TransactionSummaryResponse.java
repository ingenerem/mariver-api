package com.mariver.transaction.dto;

import java.math.BigDecimal;

public record TransactionSummaryResponse(BigDecimal totalIncome,
                                         BigDecimal totalExpenses,
                                         BigDecimal totalBillExpenses,
                                         BigDecimal totalOtherExpenses, TransactionCategoryTotal topSpendingCategory,
                                         TransactionCategoryTotal topBillCategory) {

}
