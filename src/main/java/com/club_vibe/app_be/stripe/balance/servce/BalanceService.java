package com.club_vibe.app_be.stripe.balance.servce;

import com.club_vibe.app_be.stripe.balance.dto.artist.ArtistBalanceResponse;
import com.club_vibe.app_be.stripe.balance.dto.club.ClubBalanceResponse;

public interface BalanceService {
    /**
     * Retrieves the artist's current balance with Stripe
     * @return Balance information wrapped in ArtistBalanceResponse
     */
    ArtistBalanceResponse getArtistBalance();

    /**
     * Retrieves the club's earnings for the current month
     * @return Club earnings information
     */
    ClubBalanceResponse getClubBalance();
}
