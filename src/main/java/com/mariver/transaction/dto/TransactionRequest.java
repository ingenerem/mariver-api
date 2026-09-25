package com.mariver.transaction.dto;

import com.mariver.transaction.TransactionSource;
import com.mariver.transaction.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequest (BigDecimal amount, TransactionType type,
                                  String description, String category,
                                  LocalDate transactionDate, TransactionSource transactionSource)
{
}
