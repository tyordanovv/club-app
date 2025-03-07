package com.club_vibe.app_be.stripe.accounts.dto.onboarding;

/**
 * Response for generating an onboarding link.
 */
public record GenerateOnboardingLinkResponse(
        String onboardingUrl // URL for KYC onboarding
) {}