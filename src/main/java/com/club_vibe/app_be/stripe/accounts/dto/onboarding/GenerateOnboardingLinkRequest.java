package com.club_vibe.app_be.stripe.accounts.dto.onboarding;

import jakarta.validation.constraints.NotBlank;

/**
 * Request for generating an onboarding link.
 */
public record GenerateOnboardingLinkRequest(
        @NotBlank(message = "Return URL is required")
        String returnUrl,  // URL to redirect after KYC completion

        @NotBlank(message = "Refresh URL is required")
        String refreshUrl // URL to redirect if KYC is incomplete
) {}