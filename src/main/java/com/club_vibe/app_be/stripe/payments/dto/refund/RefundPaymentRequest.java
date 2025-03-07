package com.club_vibe.app_be.stripe.payments.dto.refund;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Request for refunding a payment.
 */
public record RefundPaymentRequest(
        @NotNull(message = "Amount is required")
        @Min(value = 100, message = "Amount must be at least 100 cent")
        Long amount
) {}