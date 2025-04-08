package com.club_vibe.app_be.stripe.payout.mapper;

import com.club_vibe.app_be.common.enums.PayoutStatus;
import com.club_vibe.app_be.stripe.payout.dto.PayoutResponse;
import com.club_vibe.app_be.stripe.payout.entity.PayoutEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PayoutMapper {
    public PayoutStatus mapStripePayoutStatus(String stripeStatus) {
        return switch (stripeStatus) {
            case "pending" -> PayoutStatus.PENDING;
            case "in_transit" -> PayoutStatus.IN_TRANSIT;
            case "paid" -> PayoutStatus.COMPLETED;
            case "failed" -> PayoutStatus.FAILED;
            case "canceled" -> PayoutStatus.CANCELED;
            default -> PayoutStatus.PENDING;
        };
    }

    public List<PayoutResponse> mapPayoutsToPayoutResponse(List<PayoutEntity> payouts) {
        return payouts.stream()
                .map(payout -> new PayoutResponse(
                        payout.getId(),
                        payout.getMoneyAmount().getAmount(),
                        payout.getMoneyAmount().getCurrency(),
                        payout.getStripePayoutId(),
                        payout.getStatus(),
                        payout.getStaff().getId()))
                .collect(Collectors.toList());
    }
}
