package com.club_vibe.app_be.stripe.payments.dto.capture;

import com.club_vibe.app_be.common.util.Amount;

/**
 * Response for capturing a payment.
 *
 * @param capturedPaymentIntentId
 * @param totalAmount
 * @param capturedAmount
 * @param feeAmount
 * @param transferGroup
 */
public record CapturePaymentResponse(
        String capturedPaymentIntentId,
        Amount totalAmount,
        Amount capturedAmount,
        Amount feeAmount,
        String transferGroup
) {}