package com.club_vibe.app_be.stripe.payments.dto.capture;

/**
 * Response for capturing a payment.
 */
public record CapturePaymentResponse(
        String captureId
) {}