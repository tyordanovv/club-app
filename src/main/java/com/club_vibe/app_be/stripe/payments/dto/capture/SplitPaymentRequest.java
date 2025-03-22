package com.club_vibe.app_be.stripe.payments.dto.capture;

import com.club_vibe.app_be.common.util.Amount;

public record SplitPaymentRequest(
        Amount totalAmount,
        String transferGroup
) {
}
