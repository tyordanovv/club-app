package com.club_vibe.app_be.stripe.payments.dto.authorize;

import com.club_vibe.app_be.request.dto.reqest.InitializeRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request for authorizing a payment and containing {@link InitializeRequest} data.
 *
 * @param paymentMethodId
 * @param amount
 * @param currency
 * @param customerEmail
 * @param request
 */
public record AuthorizePaymentRequest(
        @NotBlank(message = "Stripe payment method Id is required")
        String paymentMethodId,
        @NotNull(message = "Amount is required")
        @Min(value = 20, message = "Amount must be at least 20 BGN")
        Double amount,
        @NotBlank(message = "Currency is required")
        String currency,
        @NotBlank(message = "Customer email is required")
        String customerEmail,
        @NotNull(message = "Initialize Request body is required")
        InitializeRequest request
) {}