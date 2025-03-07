package com.club_vibe.app_be.stripe.accounts.service.impl;

import com.club_vibe.app_be.stripe.accounts.dto.create.CreateConnectedAccountRequest;
import com.club_vibe.app_be.stripe.accounts.dto.onboarding.GenerateOnboardingLinkRequest;
import com.club_vibe.app_be.stripe.accounts.dto.status.AccountStatusResponse;
import com.club_vibe.app_be.stripe.accounts.dto.create.CreateConnectedAccountResponse;
import com.club_vibe.app_be.stripe.accounts.dto.onboarding.GenerateOnboardingLinkResponse;
import com.club_vibe.app_be.stripe.accounts.service.ConnectedAccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class ConnectedAccountServiceImpl implements ConnectedAccountService {
    @Override
    public CreateConnectedAccountResponse createConnectedAccount(CreateConnectedAccountRequest request) {
        return null;
    }

    @Override
    public GenerateOnboardingLinkResponse generateOnboardingLink(String accountId, GenerateOnboardingLinkRequest request) {
        return null;
    }

    @Override
    public AccountStatusResponse getAccountStatus(String accountId) {
        return null;
    }

    @Override
    public void handleStripeAccountWebhook(String payload, String sigHeader) {

    }
}
