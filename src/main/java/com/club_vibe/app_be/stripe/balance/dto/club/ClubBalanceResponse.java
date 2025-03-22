package com.club_vibe.app_be.stripe.balance.dto.club;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @param totalEarningsThisMonth
 * @param currency
 * @param eventEarnings
 */
public record ClubBalanceResponse(
        BigDecimal totalEarningsThisMonth,
        String currency,
        List<ClubEventEarning> eventEarnings
) {
}
