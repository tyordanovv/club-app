package com.club_vibe.app_be.stripe.payments.dto.authorize;

import com.club_vibe.app_be.stripe.payments.entity.StripePaymentStatus;

/**
 * Response for authorizing a payment.
 *
 * @param paymentIntentId
 * @param clientSecret
 * @param requiresAction
 */
public record AuthorizePaymentResponse(
        String paymentIntentId,
        String clientSecret,
        boolean requiresAction,
        StripePaymentStatus paymentStatus
) {}