package com.club_vibe.app_be.stripe.accounts.factory;

import com.club_vibe.app_be.stripe.accounts.dto.create.CreateConnectedAccountRequest;
import com.stripe.param.AccountCreateParams;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class StripeAccountParamsFactory {

    public static AccountCreateParams buildAccountCreateParams(CreateConnectedAccountRequest request) {
        return AccountCreateParams.builder()
                .setCountry(request.country().name())
                .setEmail(request.email())
                .setController(buildController())
                .build();
    }

    private static AccountCreateParams.Controller buildController() {
        return AccountCreateParams.Controller.builder()
                .setStripeDashboard(buildStripeDashboard())
                .setFees(buildFees())
                .setLosses(buildLosses())
                .build();
    }

    private static AccountCreateParams.Controller.StripeDashboard buildStripeDashboard() {
        return AccountCreateParams.Controller.StripeDashboard.builder()
                .setType(AccountCreateParams.Controller.StripeDashboard.Type.EXPRESS)
                .build();
    }

    private static AccountCreateParams.Controller.Fees buildFees() {
        return AccountCreateParams.Controller.Fees.builder()
                .setPayer(AccountCreateParams.Controller.Fees.Payer.APPLICATION)
                .build();
    }

    private static AccountCreateParams.Controller.Losses buildLosses() {
        return AccountCreateParams.Controller.Losses.builder()
                .setPayments(AccountCreateParams.Controller.Losses.Payments.APPLICATION)
                .build();
    }
}
