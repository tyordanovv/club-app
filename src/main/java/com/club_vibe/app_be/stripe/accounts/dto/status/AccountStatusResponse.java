package com.club_vibe.app_be.stripe.accounts.dto.status;

/**
 * Response for account status.
 */
public record AccountStatusResponse(
        boolean detailsSubmitted,
        boolean chargesEnabled,
        boolean payoutsEnabled
) {}