package com.club_vibe.app_be.stripe.accounts.factory;

import com.club_vibe.app_be.stripe.accounts.dto.onboarding.GenerateOnboardingLinkRequest;
import com.stripe.param.AccountLinkCreateParams;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class StripeAccountLinkParamsFactory {
    public static AccountLinkCreateParams buildAccountLinkCreateParams(
            String accountId, GenerateOnboardingLinkRequest request) {
        return AccountLinkCreateParams.builder()
                .setAccount(accountId)
                .setReturnUrl(request.returnUrl())
                .setRefreshUrl(request.refreshUrl())
                .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                .build();
    }
}
