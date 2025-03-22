package com.club_vibe.app_be.stripe.payments.dto;

import java.math.BigDecimal;

/**
 *
 * @param paymentId
 * @param clubConnectedAccountId
 * @param artistConnectedAccountId
 * @param clubPercentage
 * @param artistPercentage
 */
public record PaymentSplitDetails(
        Long paymentId,
        String clubConnectedAccountId,
        String artistConnectedAccountId,
        BigDecimal clubPercentage,
        BigDecimal artistPercentage
) {
}
