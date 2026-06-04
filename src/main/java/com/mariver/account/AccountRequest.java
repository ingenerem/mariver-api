package com.mariver.account;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AccountRequest(


        @NotNull(message = "Current balance is required")
        @DecimalMin(
                value = "0.00",
                message = "Current balance cannot be negative")
        BigDecimal currentBalance) {

}