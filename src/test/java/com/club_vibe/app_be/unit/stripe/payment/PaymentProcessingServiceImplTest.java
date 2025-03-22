package com.club_vibe.app_be.unit.stripe.payment;

import com.club_vibe.app_be.common.util.Amount;
import com.club_vibe.app_be.stripe.payments.dto.PaymentSplitDetails;
import com.club_vibe.app_be.stripe.payments.dto.authorize.AuthorizePaymentResponse;
import com.club_vibe.app_be.stripe.payments.dto.authorize.StripePaymentAuthorizationRequest;
import com.club_vibe.app_be.stripe.payments.dto.capture.CapturePaymentResponse;
import com.club_vibe.app_be.stripe.payments.entity.StripePaymentStatus;
import com.club_vibe.app_be.stripe.payments.service.impl.PaymentProcessingServiceImpl;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Transfer;
import com.stripe.param.PaymentIntentCaptureParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.TransferCreateParams;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentProcessingServiceImplTest {

    @InjectMocks
    private PaymentProcessingServiceImpl paymentProcessingService;

    private static final BigDecimal ARTIST_AMOUNT = BigDecimal.valueOf(60);
    private static final BigDecimal CLUB_AMOUNT = BigDecimal.valueOf(20);
    private static final String IDEMPOTENCY_KEY = UUID.randomUUID().toString();
    private static final Long PAYMENT_ID = 123L;

    @Mock
    private Logger log;

    @BeforeAll
    static void setUp() {
        mockStatic(PaymentIntent.class);

    }

    @Test
    void testAuthorizePayment_Successful() throws StripeException {
        StripePaymentAuthorizationRequest request = new StripePaymentAuthorizationRequest(
                Amount.of(100.0, "BGN"), "pm_test123", "order_123", "Test payment"
        );

        PaymentIntent mockPaymentIntent = mock(PaymentIntent.class);
        when(mockPaymentIntent.getId()).thenReturn("pi_123");
        when(mockPaymentIntent.getClientSecret()).thenReturn("secret_abc");
        when(mockPaymentIntent.getStatus()).thenReturn("requires_capture");

        when(PaymentIntent.create(any(PaymentIntentCreateParams.class))).thenReturn(mockPaymentIntent);

        AuthorizePaymentResponse response = paymentProcessingService.authorizePayment(request, IDEMPOTENCY_KEY, BigDecimal.valueOf(60), "acc_artist_123");

        assertNotNull(response);
        assertEquals("pi_123", response.paymentIntentId());
        assertEquals("secret_abc", response.clientSecret());
        assertFalse(response.requiresAction());
        assertEquals(StripePaymentStatus.AUTHENTICATED, response.paymentStatus());
    }

    @Test
    void testAuthorizePayment_StripeException() throws StripeException {
//        StripePaymentAuthorizationRequest request = new StripePaymentAuthorizationRequest(
//                new Amount(100, "BGN"), "pm_test123", "order_123", "Test payment"
//        );
//
//        mockStatic(PaymentIntent.class);
//        when(PaymentIntent.create(any(PaymentIntentCreateParams.class))).thenThrow(any(StripeException.class));
//
//        AuthorizePaymentResponse response = paymentProcessingService.authorizePayment(request);
//
//        assertNull(response.paymentIntentId());
//        assertNull(response.clientSecret());
//        assertFalse(response.requiresAction());
//        assertEquals(StripePaymentStatus.ERROR, response.paymentStatus());
    }

    @Test
    void testCapturePayment_Successful() throws StripeException {
        PaymentIntent mockPaymentIntent = mock(PaymentIntent.class);
        when(mockPaymentIntent.capture(any(PaymentIntentCaptureParams.class))).thenReturn(mockPaymentIntent);
        when(mockPaymentIntent.getId()).thenReturn("pi_123");
        when(mockPaymentIntent.getAmount()).thenReturn(1000L);
        when(mockPaymentIntent.getCurrency()).thenReturn("BGN");

        when(PaymentIntent.retrieve("pi_123")).thenReturn(mockPaymentIntent);

        CapturePaymentResponse response = paymentProcessingService.capturePayment("pi_123", IDEMPOTENCY_KEY);

        assertNotNull(response);
        assertEquals("pi_123", response.capturedPaymentIntentId());
    }

    @Test
    void testCancelPaymentAuthorization_Successful() throws StripeException {
        PaymentIntent mockPaymentIntent = mock(PaymentIntent.class);
        when((mockPaymentIntent).cancel()).thenReturn(mockPaymentIntent);

        when(PaymentIntent.retrieve("pi_123")).thenReturn(mockPaymentIntent);

        assertDoesNotThrow(() -> paymentProcessingService.cancelPaymentAuthorization("pi_123"));
    }

    @Test
    void testSplitPayments_Successful() throws StripeException {
        PaymentSplitDetails splitDetails = new PaymentSplitDetails(
                PAYMENT_ID,
                "acc_club_123",
                "acc_artist_123",
                CLUB_AMOUNT,
                ARTIST_AMOUNT
        );
        Amount totalAmount = Amount.of(100.0, "BGN");

        Transfer mockClubTransfer = mock(Transfer.class);
        Transfer mockArtistTransfer = mock(Transfer.class);
        when(mockClubTransfer.getId()).thenReturn("tr_club_123");
        when(mockArtistTransfer.getId()).thenReturn("tr_artist_123");

        mockStatic(Transfer.class);
        when(Transfer.create(any(TransferCreateParams.class))).thenReturn(mockClubTransfer, mockArtistTransfer);

        assertDoesNotThrow(() -> paymentProcessingService.splitPayments("pi_123", splitDetails, totalAmount, IDEMPOTENCY_KEY));
    }
}
