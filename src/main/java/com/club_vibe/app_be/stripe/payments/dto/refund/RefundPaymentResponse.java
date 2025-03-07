package com.club_vibe.app_be.stripe.payments.dto.refund;

/**
 * Response for refunding a payment.
 */
public record RefundPaymentResponse(
        String refundId
) {}