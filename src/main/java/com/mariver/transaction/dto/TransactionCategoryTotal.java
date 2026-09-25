package com.mariver.transaction.dto;

import java.math.BigDecimal;

public record TransactionCategoryTotal(String category,
                                       BigDecimal totalAmount) {
}
