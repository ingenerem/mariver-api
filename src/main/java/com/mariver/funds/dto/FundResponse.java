package com.mariver.funds.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FundResponse(Long id, String name, BigDecimal balance,BigDecimal availableBalance, LocalDateTime createdAt) {

}
