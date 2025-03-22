package com.club_vibe.app_be.stripe.payments.dto.authorize;

import com.club_vibe.app_be.common.util.Amount;

public record StripePaymentAuthorizationRequest(
        Amount amount,
        String paymentMethodId,
        String message,
        String orderGroup
) {
}
