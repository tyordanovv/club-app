package com.club_vibe.app_be.stripe.accounts.service;

import com.club_vibe.app_be.stripe.accounts.dto.create.CreateConnectedAccountRequest;
import com.club_vibe.app_be.stripe.accounts.dto.onboarding.GenerateOnboardingLinkRequest;
import com.club_vibe.app_be.stripe.accounts.dto.status.AccountStatusResponse;
import com.club_vibe.app_be.stripe.accounts.dto.create.CreateConnectedAccountResponse;
import com.club_vibe.app_be.stripe.accounts.dto.onboarding.GenerateOnboardingLinkResponse;

/**
 * Service for managing Stripe connected accounts.
 */
public interface ConnectedAccountService {

    /**
     * Creates a new Stripe connected account.
     *
     * @param request The request containing account details (e.g., country, email).
     * @return The response containing the Stripe account ID.
     */
    CreateConnectedAccountResponse createConnectedAccount(CreateConnectedAccountRequest request);

    /**
     * Generates an onboarding link for KYC verification.
     *
     * @param accountId The ID of the connected account.
     * @param request   The request containing URLs for redirection.
     * @return The response containing the onboarding URL.
     */
    GenerateOnboardingLinkResponse generateOnboardingLink(String accountId, GenerateOnboardingLinkRequest request);

    /**
     * Retrieves the status of a connected account (e.g., KYC completion).
     *
     * @param accountId The ID of the connected account.
     * @return The response containing account status details.
     */
    AccountStatusResponse getAccountStatus(String accountId);

    /**
     * Handles Stripe webhook events for account updates (e.g., KYC completion).
     *
     * @param payload     The raw webhook payload.
     * @param sigHeader   The Stripe-Signature header for verification.
     */
    void handleStripeAccountWebhook(String payload, String sigHeader);
}