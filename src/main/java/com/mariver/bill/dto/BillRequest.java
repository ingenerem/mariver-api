package com.mariver.bill.dto;

import com.mariver.bill.BillCategory;
import com.mariver.bill.IntervalUnit;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record BillRequest(
        @NotBlank
        String name,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal amount,

        @NotNull
        BillCategory category,

        @NotNull
        @Min(1)
        Integer intervalValue,

        @NotNull
        IntervalUnit intervalUnit,

        @NotNull
        @Min(1)
        @Max(31)
        Integer dueDay,

        @Min(1)
        @Max(12)
        Integer dueMonth )
{
}
