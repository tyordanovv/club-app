package com.club_vibe.app_be.stripe.balance.dto.club;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *
 * @param eventId
 * @param eventDate
 * @param totalAmount
 * @param currency
 * @param numberOfPayments
 * @param lastPaymentDate
 */
public record ClubEventEarning(
        Long eventId,
        LocalDateTime eventDate,
        BigDecimal totalAmount,
        String currency,
        int numberOfPayments,
        LocalDateTime lastPaymentDate
) {
}
