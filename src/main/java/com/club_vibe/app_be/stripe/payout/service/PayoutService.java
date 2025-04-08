package com.club_vibe.app_be.stripe.payout.service;

import com.club_vibe.app_be.common.enums.PayoutStatus;
import com.club_vibe.app_be.stripe.payout.dto.PayoutRequest;
import com.club_vibe.app_be.stripe.payout.dto.PayoutResponse;

import java.util.List;

public interface PayoutService {

    /**
     * Create a payout for the authenticated user's connected account
     *
     * @param request The payout request containing amount
     * @return PayoutResponse with details of the created payout
     */
    PayoutResponse createPayout(PayoutRequest request, Long authUserId, String userConnectedAccId);


    /**
     * Get all payouts for the authenticated user
     *
     * @return List of payout responses
     */
    List<PayoutResponse> getUserPayouts(Long authUserId);

    /**
     *
     * @param stripePayoutId
     * @param stripeStatus
     */
    void updatePayoutStatusByStripeId(String stripePayoutId, String stripeStatus);
}
