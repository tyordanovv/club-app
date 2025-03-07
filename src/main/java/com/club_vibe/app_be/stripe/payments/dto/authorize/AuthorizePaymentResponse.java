package com.club_vibe.app_be.stripe.payments.dto.authorize;

/**
 * Response for authorizing a payment.
 */
public record AuthorizePaymentResponse(
        String paymentIntentId,
        String clientSecret
) {}