package com.club_vibe.app_be.stripe.balance.dto.artist;

import java.math.BigDecimal;

/**
 *
 * @param currency
 * @param amountInCents
 * @param amountFormatted
 */
public record BalanceFunds(
        String currency,
        Long amountInCents,
        BigDecimal amountFormatted
) {
}
