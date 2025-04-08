package com.club_vibe.app_be.stripe.balance.servce.impl;

import com.club_vibe.app_be.common.util.TimeUtils;
import com.club_vibe.app_be.stripe.balance.dto.artist.ArtistBalanceResponse;
import com.club_vibe.app_be.stripe.balance.dto.artist.ArtistPaymentDetail;
import com.club_vibe.app_be.stripe.balance.dto.artist.ArtistPaymentDto;
import com.club_vibe.app_be.stripe.balance.dto.artist.BalanceFunds;
import com.club_vibe.app_be.stripe.balance.dto.club.ClubBalanceResponse;
import com.club_vibe.app_be.stripe.balance.dto.club.ClubEarningDto;
import com.club_vibe.app_be.stripe.balance.dto.club.ClubEventEarning;
import com.club_vibe.app_be.stripe.balance.servce.BalanceService;
import com.club_vibe.app_be.stripe.config.StripeClient;
import com.club_vibe.app_be.stripe.balance.mapper.StripeBalanceMapper;
import com.club_vibe.app_be.stripe.payments.entity.partial.PaymentAllocationType;
import com.club_vibe.app_be.stripe.payments.repository.PartialPaymentRepository;
import com.club_vibe.app_be.users.auth.service.CurrentUserService;
import com.stripe.model.Balance;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class BalanceServiceImpl implements BalanceService {
    private final CurrentUserService currentUserService;
    private final StripeClient stripeClient;
    private final PartialPaymentRepository partialPaymentRepository;
    private final StripeBalanceMapper stripeBalanceMapper;

    @Override
    public ArtistBalanceResponse getArtistBalance() {
        String stripeAccountId = currentUserService.getCurrentUserStripeAccountId();

        try {
            // Get Stripe balance
            Balance balance = stripeClient.retrieveBalance(stripeAccountId);

            List<BalanceFunds> availableFunds = balance.getAvailable().stream()
                    .map(stripeBalanceMapper::mapAvailableToBalanceFunds)
                    .collect(Collectors.toList());
            List<BalanceFunds> pendingFunds = balance.getPending().stream()
                    .map(stripeBalanceMapper::mapPendingToBalanceFunds)
                    .collect(Collectors.toList());

            // Get monthly payments from our database
            List<ArtistPaymentDetail> monthlyPayments = getArtistMonthlyPayments(stripeAccountId);

            return new ArtistBalanceResponse(availableFunds, pendingFunds, monthlyPayments);
        } catch (Exception e) {
            log.error("Failed to retrieve artist balance", e);
            throw new RuntimeException("Failed to retrieve balance from Stripe", e);
        }
    }

    @Override
    public ClubBalanceResponse getClubBalance() {
        String stripeAccountId = currentUserService.getCurrentUserStripeAccountId();

        try {
            // Find all payments allocated to this club in the current month
            List<ClubEarningDto> clubEarnings = partialPaymentRepository.findClubEarningsByMonth(
                    stripeAccountId,
                    PaymentAllocationType.CLUB,
                    TimeUtils.getFirstDayOfMonth()
            );

            BigDecimal totalEarnings = BigDecimal.ZERO;
            String currency = "BGN"; // TODO there should be no default

            // Map event earnings and calculate total earnings
            List<ClubEventEarning> eventEarnings = new ArrayList<>();

            for (ClubEarningDto earning : clubEarnings) {
                totalEarnings = totalEarnings.add(earning.getAmount());
                currency = earning.getCurrencyCode();

                eventEarnings.add(new ClubEventEarning(
                        earning.getEventId(),
                        earning.getEventDate(),
                        earning.getAmount(),
                        earning.getCurrencyCode(),
                        earning.getPaymentCount(),
                        earning.getLastPaymentDate()
                ));
            }

            return new ClubBalanceResponse(totalEarnings, currency, eventEarnings);
        } catch (Exception e) {
            log.error("Failed to retrieve club balance", e);
            throw new RuntimeException("Failed to retrieve club balance", e);
        }
    }

    private List<ArtistPaymentDetail> getArtistMonthlyPayments(String artistStripeAccountId) {
        List<ArtistPaymentDto> results = partialPaymentRepository.findArtistPaymentsByMonth(
                artistStripeAccountId,
                PaymentAllocationType.ARTIST,
                TimeUtils.getFirstDayOfMonth()
        );

        return results.stream()
                .map(dto -> new ArtistPaymentDetail(
                        dto.getPaymentId(),
                        dto.getStartTime(),
                        dto.getAmount(),
                        dto.getCurrency(),
                        dto.getPaymentDate(),
                        dto.getRequestTitle(),
                        dto.getStatus()
                ))
                .collect(Collectors.toList());
    }
}