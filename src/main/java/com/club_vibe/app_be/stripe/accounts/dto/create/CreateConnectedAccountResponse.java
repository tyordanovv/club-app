package com.club_vibe.app_be.stripe.accounts.dto.create;

/**
 *
 * @param accountId
 */
public record CreateConnectedAccountResponse(
        String accountId // Stripe account ID
) {}
