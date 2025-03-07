package com.club_vibe.app_be.stripe.accounts.dto.create;

/**
 * Response for creating a connected account.
 */
public record CreateConnectedAccountResponse(
        String accountId // Stripe account ID
) {}
