package com.club_vibe.app_be.stripe.accounts.controller;

import com.club_vibe.app_be.stripe.accounts.dto.create.CreateConnectedAccountRequest;
import com.club_vibe.app_be.stripe.accounts.dto.onboarding.GenerateOnboardingLinkRequest;
import com.club_vibe.app_be.stripe.accounts.dto.status.AccountStatusResponse;
import com.club_vibe.app_be.stripe.accounts.dto.create.CreateConnectedAccountResponse;
import com.club_vibe.app_be.stripe.accounts.dto.onboarding.GenerateOnboardingLinkResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/connected-accounts")
public class ConnectedAccountController {

    /**
     * Create a new Stripe connected account
     * @param request {@link CreateConnectedAccountRequest}
     * @return {@link CreateConnectedAccountResponse}
     */
    @PostMapping
    public ResponseEntity<CreateConnectedAccountResponse> createConnectedAccount(
            @RequestBody CreateConnectedAccountRequest request
    ) {
        // TODO
        return ResponseEntity.ok(new CreateConnectedAccountResponse(null));
    }

    /**
     * Generate onboarding link for KYC verification
     * @param accountId {@link String}
     * @param request {@link GenerateOnboardingLinkRequest}
     * @return {@link GenerateOnboardingLinkResponse}
     */
    @PostMapping("/{accountId}/onboarding-link")
    public ResponseEntity<GenerateOnboardingLinkResponse> generateOnboardingLink(
            @PathVariable String accountId,
            @RequestBody GenerateOnboardingLinkRequest request
    ) {
        // TODO
        return ResponseEntity.ok(new GenerateOnboardingLinkResponse(null));
    }

    /**
     * Get KYC status of a connected account
     * @param accountId {@link String}
     * @return {@link AccountStatusResponse}
     */
    @GetMapping("/{accountId}/status")
    public ResponseEntity<AccountStatusResponse> getAccountStatus(
            @PathVariable String accountId
    ) {
        // TODO
        return ResponseEntity.ok(new AccountStatusResponse(true, true, true));
    }

    /**
     * Webhook endpoint for Stripe account events
     * @param payload
     * @param sigHeader
     * @return {@link Void}
     */
    @PostMapping("/webhook")
    public ResponseEntity<Void> handleStripeAccountWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        // TODO
        return ResponseEntity.ok().build();
    }
}