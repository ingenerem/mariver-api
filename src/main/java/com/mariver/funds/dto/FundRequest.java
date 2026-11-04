package com.mariver.funds.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FundRequest(
        @NotBlank(message = "Fund name is required")
        @Size(max = 100)
        String name) {
}
