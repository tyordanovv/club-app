package com.club_vibe.app_be.unit.stripe.payment;

import com.club_vibe.app_be.common.exception.ItemNotFoundException;
import com.club_vibe.app_be.stripe.payments.dto.PaymentSplitDetails;
import com.club_vibe.app_be.stripe.payments.dto.authorize.AuthorizePaymentResponse;
import com.club_vibe.app_be.stripe.payments.entity.PaymentEntity;
import com.club_vibe.app_be.stripe.payments.entity.StripePaymentStatus;
import com.club_vibe.app_be.stripe.payments.repository.PaymentRepository;
import com.club_vibe.app_be.stripe.payments.service.impl.PaymentDataAccessServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentDataAccessServiceImplTest {
    private static final Long PAYMENT_ID = 123L;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentDataAccessServiceImpl paymentDataAccessService;

    private static final BigDecimal ARTIST_PERCENTAGE = BigDecimal.valueOf(60);
    private static final BigDecimal CLUB_PERCENTAGE = BigDecimal.valueOf(20);

    @Test
    void testUpdatePaymentAfterAuthentication_Success() {
        // Given
        PaymentEntity payment = new PaymentEntity();
        AuthorizePaymentResponse authResponse = new AuthorizePaymentResponse(
                "pi_123",
                "client_secret_123",
                false,
                StripePaymentStatus.AUTHENTICATED
        );

        // When
        paymentDataAccessService.updatePaymentAfterAuthentication(payment, authResponse);

        // Then
        assertEquals("pi_123", payment.getStripePaymentIntentId());
        assertEquals(StripePaymentStatus.AUTHENTICATED, payment.getStatus());
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void testGetPaymentSplitData_Success() {
        // Given
        String paymentIntentId = "pi_123";
        PaymentSplitDetails splitDetails = new PaymentSplitDetails(
                PAYMENT_ID, "acc_club", "acc_artist", CLUB_PERCENTAGE, ARTIST_PERCENTAGE);

        when(paymentRepository.findPaymentSplitDataByPaymentIntentId(paymentIntentId))
                .thenReturn(Optional.of(splitDetails));

        // When
        PaymentSplitDetails result = paymentDataAccessService.getPaymentSplitData(paymentIntentId);

        // Then
        assertNotNull(result);
        assertEquals("acc_club", result.clubConnectedAccountId());
        assertEquals("acc_artist", result.artistConnectedAccountId());
        assertEquals(CLUB_PERCENTAGE, result.clubPercentage());
        assertEquals(ARTIST_PERCENTAGE, result.artistPercentage());
        verify(paymentRepository, times(1)).findPaymentSplitDataByPaymentIntentId(paymentIntentId);
    }

    @Test
    void testGetPaymentSplitData_NotFound_ShouldThrowException() {
        // Given
        String paymentIntentId = "pi_123";
        when(paymentRepository.findPaymentSplitDataByPaymentIntentId(paymentIntentId))
                .thenReturn(Optional.empty());

        // Then
        assertThrows(ItemNotFoundException.class, () -> {
            paymentDataAccessService.getPaymentSplitData(paymentIntentId);
        });
    }

    @Test
    void testGetPaymentSplitData_MissingClubAccountId_ShouldThrowException() {
        // Given
        String paymentIntentId = "pi_123";
        PaymentSplitDetails splitDetails = new PaymentSplitDetails(PAYMENT_ID, "", "acc_artist", CLUB_PERCENTAGE, ARTIST_PERCENTAGE);

        when(paymentRepository.findPaymentSplitDataByPaymentIntentId(paymentIntentId))
                .thenReturn(Optional.of(splitDetails));

        // Then
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            paymentDataAccessService.getPaymentSplitData(paymentIntentId);
        });

        assertTrue(exception.getMessage().contains("Club connected account ID is missing"));
    }

    @Test
    void testGetPaymentSplitData_MissingArtistAccountId_ShouldThrowException() {
        // Given
        String paymentIntentId = "pi_123";
        PaymentSplitDetails splitDetails = new PaymentSplitDetails(PAYMENT_ID, "acc_club", "", CLUB_PERCENTAGE, ARTIST_PERCENTAGE);

        when(paymentRepository.findPaymentSplitDataByPaymentIntentId(paymentIntentId))
                .thenReturn(Optional.of(splitDetails));

        // Then
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            paymentDataAccessService.getPaymentSplitData(paymentIntentId);
        });

        assertTrue(exception.getMessage().contains("Artist connected account ID is missing"));
    }

    @Test
    void testGetPaymentSplitData_InvalidPercentages_ShouldThrowException() {
        // Given
        String paymentIntentId = "pi_123";
        PaymentSplitDetails splitDetails = new PaymentSplitDetails(
                PAYMENT_ID, "acc_club", "acc_artist", BigDecimal.valueOf(70), BigDecimal.valueOf(50));

        when(paymentRepository.findPaymentSplitDataByPaymentIntentId(paymentIntentId))
                .thenReturn(Optional.of(splitDetails));

        // Then
        assertThrows(IllegalArgumentException.class, () -> {
            paymentDataAccessService.getPaymentSplitData(paymentIntentId);
        });
    }
}
