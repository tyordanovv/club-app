package com.club_vibe.app_be.stripe.accounts.service;

import com.club_vibe.app_be.stripe.accounts.dto.create.CreateConnectedAccountRequest;
import com.club_vibe.app_be.stripe.accounts.dto.onboarding.GenerateOnboardingLinkRequest;
import com.club_vibe.app_be.stripe.accounts.dto.status.AccountStatusResponse;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;

/**
 * Service for managing Stripe connected accounts.
 */
public interface ConnectedAccountService {

    /**
     * Creates a new Stripe connected account.
     *
     * @param request {@link CreateConnectedAccountRequest} The request containing account details (e.g., country, email).
     * @return {@link String} The response containing the Stripe account ID.
     * @throws StripeException
     */
    String createConnectedAccount(CreateConnectedAccountRequest request) throws StripeException;

    /**
     * Generates an onboarding link for KYC verification.
     *
     * @param accountId {@link String} The ID of the connected account.
     * @param request {@link GenerateOnboardingLinkRequest} The request containing URLs for redirection.
     * @return {@link String} The response containing the onboarding URL.
     * @throws StripeException
     */
    String generateOnboardingLink(String accountId, GenerateOnboardingLinkRequest request) throws StripeException;

    /**
     * Retrieves the status of a connected account (e.g., KYC completion).
     *
     * @param accountId The ID of the connected account.
     * @return The response containing account status details.
     */
    AccountStatusResponse getAccountStatus(String accountId) throws StripeException;

    /**
     * Checks KYC status of the user and updates the data in the database.
     *
     * @param account {@link Account} The user stripe account
     */
    void updateStripeAccount(Account account) throws SignatureVerificationException;
}