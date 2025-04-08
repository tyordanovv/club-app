package com.club_vibe.app_be.unit.stripe.balance;

import com.club_vibe.app_be.stripe.balance.dto.artist.ArtistBalanceResponse;
import com.club_vibe.app_be.stripe.balance.dto.artist.ArtistPaymentDto;
import com.club_vibe.app_be.stripe.balance.dto.artist.BalanceFunds;
import com.club_vibe.app_be.stripe.balance.dto.club.ClubBalanceResponse;
import com.club_vibe.app_be.stripe.balance.dto.club.ClubEarningDto;
import com.club_vibe.app_be.stripe.config.StripeClient;
import com.club_vibe.app_be.stripe.balance.servce.impl.BalanceServiceImpl;
import com.club_vibe.app_be.stripe.balance.mapper.StripeBalanceMapper;
import com.club_vibe.app_be.stripe.payments.entity.StripePaymentStatus;
import com.club_vibe.app_be.stripe.payments.entity.partial.PaymentAllocationType;
import com.club_vibe.app_be.stripe.payments.repository.PartialPaymentRepository;
import com.club_vibe.app_be.users.auth.service.CurrentUserService;
import com.stripe.model.Balance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceImplTest {
    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private StripeClient stripeClient;

    @Mock
    private PartialPaymentRepository partialPaymentRepository;

    @Mock
    private StripeBalanceMapper stripeBalanceMapper;

    @InjectMocks
    private BalanceServiceImpl balanceService;

    private final String testStripeAccountId = "acct_test123";

    @BeforeEach
    void setUp() {
        when(currentUserService.getCurrentUserStripeAccountId()).thenReturn(testStripeAccountId);
    }

    @Test
    void getArtistBalance_shouldReturnCorrectResponse() throws Exception {
        // Given
        Balance mockBalance = new Balance();

        Balance.Available availableBalance = new Balance.Available();
        availableBalance.setAmount(10000L); // $100.00
        availableBalance.setCurrency("BGN");

        Balance.Pending pendingBalance = new Balance.Pending();
        pendingBalance.setAmount(5000L); // $50.00
        pendingBalance.setCurrency("BGN");

        // Set Balance properties using reflection since Balance class has limited public API
        mockBalance.setAvailable(Collections.singletonList(availableBalance));
        mockBalance.setPending(Collections.singletonList(pendingBalance));

        when(stripeClient.retrieveBalance(testStripeAccountId)).thenReturn(mockBalance);

        BalanceFunds availableFunds = new BalanceFunds("BGN", 10000L, BigDecimal.valueOf(100.00));
        BalanceFunds pendingFunds = new BalanceFunds("BGN", 5000L, BigDecimal.valueOf(50.00));

        when(stripeBalanceMapper.mapAvailableToBalanceFunds(availableBalance)).thenReturn(availableFunds);
        when(stripeBalanceMapper.mapPendingToBalanceFunds(pendingBalance)).thenReturn(pendingFunds);

        List<ArtistPaymentDto> mockPayments = List.of(
                new ArtistPaymentDto(
                        1L, LocalDateTime.now(),
                        BigDecimal.valueOf(70.00), "BGN",
                        LocalDateTime.now(), "Test Request", StripePaymentStatus.FINISHED
                )
        );

        when(partialPaymentRepository.findArtistPaymentsByMonth(
                eq(testStripeAccountId),
                eq(PaymentAllocationType.ARTIST),
                any(LocalDateTime.class))
        ).thenReturn(mockPayments);

        // When
        ArtistBalanceResponse result = balanceService.getArtistBalance();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.available()).hasSize(1);
        assertThat(result.available().get(0).amountFormatted()).isEqualByComparingTo(BigDecimal.valueOf(100.00));
        assertThat(result.pending()).hasSize(1);
        assertThat(result.pending().get(0).amountFormatted()).isEqualByComparingTo(BigDecimal.valueOf(50.00));
        assertThat(result.monthlyPayments()).hasSize(1);
        assertThat(result.monthlyPayments().get(0).amount()).isEqualByComparingTo(BigDecimal.valueOf(70.00));
    }

    @Test
    void getClubBalance_shouldReturnCorrectResponse() {
        // Given
        List<ClubEarningDto> mockEarnings = Arrays.asList(
                new ClubEarningDto(
                        1L, LocalDateTime.now(),
                        BigDecimal.valueOf(200.00), "BGN",
                        5, LocalDateTime.now()
                ),
                new ClubEarningDto(
                        2L, LocalDateTime.now(),
                        BigDecimal.valueOf(150.00), "BGN",
                        3, LocalDateTime.now()
                )
        );

        when(partialPaymentRepository.findClubEarningsByMonth(
                eq(testStripeAccountId),
                eq(PaymentAllocationType.CLUB),
                any(LocalDateTime.class))
        ).thenReturn(mockEarnings);

        // When
        ClubBalanceResponse result = balanceService.getClubBalance();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.totalEarningsThisMonth()).isEqualByComparingTo(BigDecimal.valueOf(350.00)); // 200 + 150
        assertThat(result.currency()).isEqualTo("BGN");
        assertThat(result.eventEarnings()).hasSize(2);
        assertThat(result.eventEarnings().get(0).totalAmount()).isEqualByComparingTo(BigDecimal.valueOf(200.00));
        assertThat(result.eventEarnings().get(1).totalAmount()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
    }
}
