package com.club_vibe.app_be.users.artist.dto;

import com.club_vibe.app_be.users.staff.entity.StripeDetails;

import java.math.BigDecimal;

public record ArtistDetails(
        Long id,
        String name,
        StripeDetails stripeDetails,
        BigDecimal percentage
) {}
