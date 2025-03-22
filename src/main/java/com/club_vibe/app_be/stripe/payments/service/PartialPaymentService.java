package com.club_vibe.app_be.stripe.payments.service;

import com.club_vibe.app_be.common.util.Amount;
import com.club_vibe.app_be.stripe.payments.dto.PaymentSplitDetails;

public interface PartialPaymentService {
    /**
     * Records partial payment allocations for club, artist, platform, and fee.
     *
     * @param splitDetails      contains clubPercentage, artistPercentage, etc.
     * @param totalAmount       the total amount captured.
     * @param capturedAmount    the actual amount captured after fee.
     */
    void recordAllocations(PaymentSplitDetails splitDetails, Amount totalAmount, Amount capturedAmount);
}
