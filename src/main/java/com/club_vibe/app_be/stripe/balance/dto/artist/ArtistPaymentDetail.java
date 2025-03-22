package com.club_vibe.app_be.stripe.balance.dto.artist;

import com.club_vibe.app_be.stripe.payments.entity.StripePaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *
 * @param paymentId
 * @param eventDate
 * @param amount
 * @param currency
 * @param paymentDate
 * @param requestTitle
 * @param status
 */
public record ArtistPaymentDetail(
        Long paymentId,
        LocalDateTime eventDate,
        BigDecimal amount,
        String currency,
        LocalDateTime paymentDate,
        String requestTitle,
        StripePaymentStatus status
) {
}
