package com.club_vibe.app_be.stripe.payments.dto.authorize;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request for authorizing a payment.
 */
public record AuthorizePaymentRequest(
        @NotNull(message = "Amount is required")
        @Min(value = 1, message = "Amount must be at least 10 euro")
        Long amount,

        @NotBlank(message = "Currency is required")
        String currency,

        String customerId
) {}