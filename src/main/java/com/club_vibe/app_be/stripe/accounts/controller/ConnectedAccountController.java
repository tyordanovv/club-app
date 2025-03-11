package com.club_vibe.app_be.stripe.accounts.controller;

import com.club_vibe.app_be.stripe.accounts.dto.create.CreateConnectedAccountRequest;
import com.club_vibe.app_be.stripe.accounts.dto.onboarding.GenerateOnboardingLinkRequest;
import com.club_vibe.app_be.stripe.accounts.dto.status.AccountStatusResponse;
import com.club_vibe.app_be.stripe.accounts.dto.create.CreateConnectedAccountResponse;
import com.club_vibe.app_be.stripe.accounts.dto.onboarding.GenerateOnboardingLinkResponse;
import com.club_vibe.app_be.stripe.accounts.service.ConnectedAccountService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/connected-accounts")
public class ConnectedAccountController {
    private final ConnectedAccountService connectedAccountService;

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
    ) throws StripeException {
        String onboardingUrl = connectedAccountService.generateOnboardingLink(accountId, request);
        return ResponseEntity.ok(new GenerateOnboardingLinkResponse(onboardingUrl));
    }

    /**
     * Get KYC status of a connected account
     * @param accountId {@link String}
     * @return {@link AccountStatusResponse}
     */
    @GetMapping("/{accountId}/status")
    public ResponseEntity<AccountStatusResponse> getAccountStatus(
            @PathVariable String accountId
    ) throws StripeException {
        AccountStatusResponse status = connectedAccountService.getAccountStatus(accountId);
        return ResponseEntity.ok(status);
    }
}