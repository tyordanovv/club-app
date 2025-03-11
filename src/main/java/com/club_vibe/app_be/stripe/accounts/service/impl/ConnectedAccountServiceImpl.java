package com.club_vibe.app_be.stripe.accounts.service.impl;

import com.club_vibe.app_be.stripe.accounts.dto.create.CreateConnectedAccountRequest;
import com.club_vibe.app_be.stripe.accounts.dto.onboarding.GenerateOnboardingLinkRequest;
import com.club_vibe.app_be.stripe.accounts.dto.status.AccountStatusResponse;
import com.club_vibe.app_be.stripe.accounts.service.ConnectedAccountService;
import com.club_vibe.app_be.users.staff.dto.UpdateStripeDetailsRequest;
import com.club_vibe.app_be.users.staff.entity.KycStatus;
import com.club_vibe.app_be.users.staff.service.StaffService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class ConnectedAccountServiceImpl implements ConnectedAccountService {
    private final StaffService staffService;

    @Override
    public String createConnectedAccount(CreateConnectedAccountRequest request)
            throws StripeException {
        log.info("Initializing account creation for user {} at country {}",
                request.email(), request.country().name());
        Account account = Account.create(
                AccountCreateParams.builder()
                        .setCountry(request.country().name())
                        .setEmail(request.email())
                        .setController(
                                AccountCreateParams.Controller.builder()
                                        .setStripeDashboard(
                                                AccountCreateParams.Controller.StripeDashboard.builder()
                                                        .setType(AccountCreateParams.Controller.StripeDashboard.Type.EXPRESS)
                                                        .build())
                                        .setFees(
                                                AccountCreateParams.Controller.Fees.builder()
                                                        .setPayer(AccountCreateParams.Controller.Fees.Payer.APPLICATION)
                                                        .build())
                                        .setLosses(
                                                AccountCreateParams.Controller.Losses.builder()
                                                        .setPayments(AccountCreateParams.Controller.Losses.Payments.APPLICATION)
                                                        .build())
                                        .build())
                .build()
        );
        log.info("Created account for user {} with connectedAccountId = {}", request.email(), account.getId());
        return account.getId();
    }

    @Override
    public String generateOnboardingLink(String accountId, GenerateOnboardingLinkRequest request)
            throws StripeException {
        log.info("Initializing onboarding link creation for connected account {}", accountId);
        AccountLink accountLink = AccountLink.create(
                AccountLinkCreateParams.builder()
                        .setAccount(accountId)
                        .setReturnUrl(request.returnUrl())
                        .setRefreshUrl(request.refreshUrl())
                        .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                        .build()
        );
        log.info("Created onboarding link for connected account {}: {}", accountId, accountLink.getUrl());
        return accountLink.getUrl();
    }

    @Override
    public AccountStatusResponse getAccountStatus(String accountId) throws StripeException {
        Account account = Account.retrieve(accountId);
        return new AccountStatusResponse(
                account.getDetailsSubmitted(), // KYC completed?
                account.getChargesEnabled(),   // Can accept payments?
                account.getPayoutsEnabled()    // Can receive payouts?
        );
    }

    @Override
    public void updateStripeAccount(Account account) {
        if (account.getDetailsSubmitted()) {
            staffService.updateStripeDetails(
                    new UpdateStripeDetailsRequest(account.getEmail(), account.getId(), KycStatus.VERIFIED));
        }
    }
}
