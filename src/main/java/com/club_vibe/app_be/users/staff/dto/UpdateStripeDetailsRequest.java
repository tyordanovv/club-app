package com.club_vibe.app_be.users.staff.dto;

import com.club_vibe.app_be.users.staff.entity.KycStatus;

public record UpdateStripeDetailsRequest(
        String email,
        String stripeAccountId,
        KycStatus status
) {}
