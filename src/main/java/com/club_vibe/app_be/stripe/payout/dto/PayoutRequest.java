package com.club_vibe.app_be.stripe.payout.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 *
 * @param accountId
 * @param amount
 * @param currency
 */
public record PayoutRequest(
    @NotNull(message = "Account ID is required")
    Long accountId,

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    BigDecimal amount,

    @NotBlank
    String currency
) {
}
