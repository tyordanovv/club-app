package com.club_vibe.app_be.stripe.payout.dto;

import com.club_vibe.app_be.common.enums.PayoutStatus;

public record PayoutStatusResponse(
        Long id,
        PayoutStatus status
) {
}
