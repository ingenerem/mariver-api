package com.mariver.transaction.dto;

import com.mariver.transaction.TransactionCategory;
import com.mariver.transaction.TransactionStatus;
import com.mariver.transaction.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TransactionResponse(Long id, BigDecimal amount, TransactionType type,
                                  TransactionCategory category, String description,
                                  LocalDate transactionDate, TransactionStatus status,LocalDateTime createdAt) {
}
