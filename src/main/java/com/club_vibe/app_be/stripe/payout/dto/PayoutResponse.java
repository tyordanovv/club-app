package com.club_vibe.app_be.stripe.payout.dto;

import com.club_vibe.app_be.common.enums.PayoutStatus;

import java.math.BigDecimal;

public record PayoutResponse(
        Long id,
        BigDecimal amount,
        String currency,
        String stripePayoutId,
        PayoutStatus status,
        Long accountId
) {
}
