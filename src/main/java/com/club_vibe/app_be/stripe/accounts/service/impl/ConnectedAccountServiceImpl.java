package com.club_vibe.app_be.stripe.accounts.service.impl;

import com.club_vibe.app_be.stripe.accounts.dto.create.CreateConnectedAccountRequest;
import com.club_vibe.app_be.stripe.accounts.dto.onboarding.GenerateOnboardingLinkRequest;
import com.club_vibe.app_be.stripe.accounts.dto.status.AccountStatusResponse;
import com.club_vibe.app_be.stripe.accounts.factory.StripeAccountLinkParamsFactory;
import com.club_vibe.app_be.stripe.accounts.factory.StripeAccountParamsFactory;
import com.club_vibe.app_be.stripe.accounts.service.ConnectedAccountService;
import com.club_vibe.app_be.users.staff.dto.UpdateStripeDetailsRequest;
import com.club_vibe.app_be.users.staff.entity.KycStatus;
import com.club_vibe.app_be.users.staff.service.StaffService;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class ConnectedAccountServiceImpl implements ConnectedAccountService {

    private final StaffService staffService;

    @Override
    public String createConnectedAccount(CreateConnectedAccountRequest request) throws StripeException {
        log.info("Initializing account creation for user {} at country {}",
                request.email(), request.country().name());
        Account account = Account.create(StripeAccountParamsFactory.buildAccountCreateParams(request));
        log.info("Created account for user {} with connectedAccountId = {}", request.email(), account.getId());
        return account.getId();
    }

    @Override
    public String generateOnboardingLink(String accountId, GenerateOnboardingLinkRequest request)
            throws StripeException {
        log.info("Initializing onboarding link creation for connected account {}", accountId);
        AccountLink accountLink = AccountLink.create(
                StripeAccountLinkParamsFactory.buildAccountLinkCreateParams(accountId, request)
        );
        log.info("Created onboarding link for connected account {}: {}", accountId, accountLink.getUrl());
        return accountLink.getUrl();
    }

    @Override
    public AccountStatusResponse getAccountStatus(String accountId) throws StripeException {
        Account account = Account.retrieve(accountId);
        return new AccountStatusResponse(
                account.getDetailsSubmitted(),
                account.getChargesEnabled(),
                account.getPayoutsEnabled()
        );
    }

    @Override
    public void updateStripeAccount(Account account) {
        if (account.getDetailsSubmitted()) {
            staffService.updateStripeDetails(
                    new UpdateStripeDetailsRequest(account.getEmail(), account.getId(), KycStatus.VERIFIED)
            );
        }
    }
}
