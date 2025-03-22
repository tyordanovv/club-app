package com.club_vibe.app_be.stripe.payments.service.impl;

import com.club_vibe.app_be.common.util.Amount;
import com.club_vibe.app_be.common.util.DefaultPlatformValues;
import com.club_vibe.app_be.stripe.payments.dto.PaymentSplitDetails;
import com.club_vibe.app_be.stripe.payments.entity.PaymentEntity;
import com.club_vibe.app_be.stripe.payments.entity.partial.PartialPaymentEntity;
import com.club_vibe.app_be.stripe.payments.entity.partial.PaymentAllocationType;
import com.club_vibe.app_be.stripe.payments.repository.PartialPaymentRepository;
import com.club_vibe.app_be.stripe.payments.service.PartialPaymentService;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

@Service
@AllArgsConstructor
@Slf4j
public class PartialPaymentServiceImpl implements PartialPaymentService {
    private final PartialPaymentRepository partialPaymentRepository;
    private final EntityManager entityManager;

    /**
     * Records partial payment allocations for club, artist, platform, and fee.
     *
     * @param splitDetails contains clubPercentage, artistPercentage, etc.
     * @param totalAmount  the total amount captured.
     */
    @Override
    public void recordAllocations(PaymentSplitDetails splitDetails, Amount totalAmount, Amount capturedAmount) {
        LocalDateTime now = LocalDateTime.now();

        // Calculate amounts using your existing Amount calculations
        Amount clubAmount = totalAmount.calculatePercentage(splitDetails.clubPercentage());
        Amount artistAmount = totalAmount.calculatePercentage(splitDetails.artistPercentage());
        Amount platformAmount = calculatePlatformAmount(capturedAmount, artistAmount, clubAmount);
        Amount feeAmount = calculateFeeAmount(totalAmount, capturedAmount);

        // Build the PaymentAllocation records.
        PartialPaymentEntity clubAllocation = PartialPaymentEntity.builder()
                .type(PaymentAllocationType.CLUB)
                .amount(clubAmount.getValue())
                .currency(clubAmount.getCurrency())
                .allocationDate(now)
                .payment(entityManager.getReference(PaymentEntity.class, splitDetails.paymentId()))
                .description("Club payout")
                .build();

        PartialPaymentEntity artistAllocation = PartialPaymentEntity.builder()
                .type(PaymentAllocationType.ARTIST)
                .amount(artistAmount.getValue())
                .currency(artistAmount.getCurrency())
                .allocationDate(now)
                .payment(entityManager.getReference(PaymentEntity.class, splitDetails.paymentId()))
                .description("Artist payout")
                .build();

        PartialPaymentEntity platformAllocation = PartialPaymentEntity.builder()
                .type(PaymentAllocationType.PLATFORM)
                .amount(platformAmount.getValue())
                .currency(platformAmount.getCurrency())
                .allocationDate(now)
                .payment(entityManager.getReference(PaymentEntity.class, splitDetails.paymentId()))
                .description("Platform retention")
                .build();

        PartialPaymentEntity feeAllocation = PartialPaymentEntity.builder()
                .type(PaymentAllocationType.FEE)
                .amount(feeAmount.getValue())
                .currency(feeAmount.getCurrency())
                .allocationDate(now)
                .payment(entityManager.getReference(PaymentEntity.class, splitDetails.paymentId()))
                .description("Processing fee")
                .build();

        partialPaymentRepository.saveAll(Arrays.asList(clubAllocation, artistAllocation, platformAllocation, feeAllocation));
        log.info("Recorded partial payments for PaymentId {}: Club={}, Artist={}, Platform={}, Fee={}",
                splitDetails.paymentId(),
                clubAllocation.getAmount(),
                artistAllocation.getAmount(),
                platformAllocation.getAmount(),
                feeAllocation.getAmount());
    }

    private Amount calculateFeeAmount(Amount totalAmount, Amount capturedAmount) {
        long feeAmountInCents = totalAmount.toCents() - capturedAmount.toCents();
        String currency = capturedAmount.getCurrency();
        if (feeAmountInCents < 0) {
            log.error("The fee amount should not be less than 0. Value {}. " +
                    "The fee will be set as 0!", feeAmountInCents);
            feeAmountInCents = 0L;
        }
        return Amount.fromCents(feeAmountInCents, currency);
    }

    private Amount calculatePlatformAmount(Amount capturedAmount, Amount artistAmount, Amount clubAmount) {
        long platformAmountInCents = capturedAmount.toCents() - clubAmount.toCents() - artistAmount.toCents();
        String currency = capturedAmount.getCurrency();
        if (platformAmountInCents < 0) {
            log.error("The platform amount should not be less than 0. Value {}. " +
                    "The amount will be set as 0!", platformAmountInCents);
            platformAmountInCents = 0L;
        }
        return Amount.fromCents(platformAmountInCents, currency);
    }
}
