package com.club_vibe.app_be.users.club.dto;

import com.club_vibe.app_be.users.staff.entity.StripeDetails;

import java.math.BigDecimal;

public record ClubDetails(
        Long id,
        String clubName,
        StripeDetails stripeDetails,
        BigDecimal percentage
) {
}
