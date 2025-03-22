package com.club_vibe.app_be.stripe.payments.dto;

import com.club_vibe.app_be.common.util.Amount;

import java.util.Currency;

/**
 *
 * @param amount
 * @param requestId
 * @param currency
 */
public record CreatePaymentRequest(
        Amount amount,
        Long requestId,
        Currency currency
) {
}
