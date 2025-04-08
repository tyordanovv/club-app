package com.club_vibe.app_be.stripe.payout.service.impl;

import com.club_vibe.app_be.common.embedable.MoneyAmount;
import com.club_vibe.app_be.common.enums.PayoutStatus;
import com.club_vibe.app_be.common.util.Amount;
import com.club_vibe.app_be.stripe.config.StripeClient;
import com.club_vibe.app_be.stripe.payout.dto.PayoutRequest;
import com.club_vibe.app_be.stripe.payout.dto.PayoutResponse;
import com.club_vibe.app_be.stripe.payout.entity.PayoutEntity;
import com.club_vibe.app_be.stripe.payout.mapper.PayoutMapper;
import com.club_vibe.app_be.stripe.payout.repository.PayoutRepository;
import com.club_vibe.app_be.stripe.payout.service.PayoutService;
import com.club_vibe.app_be.users.auth.service.CurrentUserService;
import com.club_vibe.app_be.users.staff.entity.StaffEntity;
import com.stripe.exception.StripeException;
import com.stripe.model.Payout;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class PayoutServiceImpl implements PayoutService {
    private final PayoutMapper payoutMapper;
    private final PayoutRepository payoutRepository;
    private final StripeClient stripeClient;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public PayoutResponse createPayout(PayoutRequest request, Long authenticatedUserId, String stripeAccountId) {
        log.info("Creating payout for authenticated user: {}", request);

        // Verify the account has a valid Stripe account ID
        if (stripeAccountId == null || stripeAccountId.isEmpty()) {
            throw new RuntimeException("Your account doesn't have a valid Stripe account ID");
        }

        PayoutEntity payout = new PayoutEntity();
        Amount amount = Amount.of(request.amount(), request.currency());

        payout.setMoneyAmount(amount.toMoneyAmount());
        payout.setStatus(PayoutStatus.PENDING);
        payout.setStaff(entityManager.getReference(StaffEntity.class, authenticatedUserId));

        try {
            Payout stripePayout = stripeClient.createPayout(
                    stripeAccountId,
                    amount.toCents(),
                    amount.getCurrency(),
                    "Payout for user " + authenticatedUserId
            );

            payout.setStripePayoutId(stripePayout.getId());
            payout.setStatus(payoutMapper.mapStripePayoutStatus(stripePayout.getStatus()));
            payoutRepository.save(payout);

            return new PayoutResponse(
                    payout.getId(),
                    payout.getMoneyAmount().getAmount(),
                    payout.getMoneyAmount().getCurrency(),
                    payout.getStripePayoutId(),
                    payout.getStatus(),
                    authenticatedUserId);

        } catch (StripeException e) {
            log.error("Failed to create Stripe payout", e);
            throw new RuntimeException("Failed to create payout: " + e.getMessage());
        }
    }

    @Override
    public List<PayoutResponse> getUserPayouts(Long authenticatedUserId) {
        List<PayoutEntity> payouts = payoutRepository.findByStaffIdOrderByIdDesc(authenticatedUserId);

        return payoutMapper.mapPayoutsToPayoutResponse(payouts);
    }

    @Override
    @Transactional
    public void updatePayoutStatusByStripeId(String stripePayoutId, String stripeWebhookStatus) {
        PayoutEntity payout = payoutRepository.findByStripePayoutId(stripePayoutId)
                .orElseThrow(() -> new RuntimeException("Payout not found with Stripe ID: " + stripePayoutId));

        PayoutStatus newStatus = payoutMapper.mapStripePayoutStatus(stripeWebhookStatus);

        if (payout.getStatus() != newStatus) {
            log.info("Updating payout status from {} to {} for Stripe payout ID: {}",
                    payout.getStatus(), newStatus, stripePayoutId);
            payout.setStatus(newStatus);
            payoutRepository.save(payout);
        }
    }
}
