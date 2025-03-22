package com.club_vibe.app_be.stripe.balance.dto.artist;

import java.util.List;

/**
 *
 * @param available
 * @param pending
 * @param monthlyPayments
 */
public record ArtistBalanceResponse(
        List<BalanceFunds> available,
        List<BalanceFunds> pending,
        List<ArtistPaymentDetail> monthlyPayments
) {
}
