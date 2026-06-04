package com.mariver.transaction.dto;

import com.mariver.transaction.TransactionCategory;
import com.mariver.transaction.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequest (BigDecimal amount, TransactionType type,
                                 TransactionCategory category, String description,
                                 LocalDate transactionDate)
{
}
