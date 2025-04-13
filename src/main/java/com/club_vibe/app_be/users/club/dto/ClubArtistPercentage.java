package com.club_vibe.app_be.users.club.dto;

import java.math.BigDecimal;

public record ClubArtistPercentage(
        BigDecimal artistPercentage,
        BigDecimal clubPercentage
) {
}
