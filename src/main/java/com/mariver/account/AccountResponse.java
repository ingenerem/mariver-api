package com.mariver.account;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(

        Long id,
        BigDecimal currentBalance,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
