package com.club_vibe.app_be.unit.stripe.payment;

import com.club_vibe.app_be.common.util.Amount;
import com.club_vibe.app_be.events.service.EventService;
import com.club_vibe.app_be.request.service.RequestService;
import com.club_vibe.app_be.unit.stripe.AbstractPaymentTest;
import com.club_vibe.app_be.stripe.payments.dto.PaymentSplitDetails;
import com.club_vibe.app_be.stripe.payments.dto.authorize.AuthorizePaymentRequest;
import com.club_vibe.app_be.stripe.payments.dto.authorize.AuthorizePaymentResponse;
import com.club_vibe.app_be.stripe.payments.dto.authorize.StripePaymentAuthorizationRequest;
import com.club_vibe.app_be.stripe.payments.dto.capture.CapturePaymentResponse;
import com.club_vibe.app_be.stripe.payments.entity.PaymentEntity;
import com.club_vibe.app_be.stripe.payments.service.PartialPaymentService;
import com.club_vibe.app_be.stripe.payments.service.PaymentDataAccessService;
import com.club_vibe.app_be.stripe.payments.service.PaymentProcessingService;
import com.club_vibe.app_be.stripe.payments.service.impl.PaymentManagementServiceImpl;
import com.stripe.exception.StripeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentManagementServiceImplUnitTest extends AbstractPaymentTest {
    @Mock
    private EventService eventService;
    @Mock
    private RequestService requestService;
    @Mock
    private PaymentProcessingService paymentProcessingService;
    @Mock
    private PaymentDataAccessService paymentDataAccessService;
    @Mock
    private PartialPaymentService partialPaymentService;

    @InjectMocks
    private PaymentManagementServiceImpl paymentManagementService;


    @Test
    public void testAuthorizePayment_success() throws StripeException {
        // Arrange
        AuthorizePaymentRequest authRequest = createAuthorizePaymentrequest();
        PaymentEntity dummyPayment = new PaymentEntity();
        when(paymentDataAccessService.createPayment(any())).thenReturn(dummyPayment);
        when(requestService.initializeRequest(any())).thenReturn(1L);
        AuthorizePaymentResponse dummyAuthResponse = createAuthorizePaymentResponse();
        when(paymentProcessingService.authorizePayment(any(StripePaymentAuthorizationRequest.class), any(), any(), any()))
                .thenReturn(dummyAuthResponse);

        // Act
        AuthorizePaymentResponse response = paymentManagementService.authorizePayment(authRequest, any());

        // Assert
        assertNotNull(response);
        assertEquals(PAYMENT_INTEND_ID, response.paymentIntentId());
        verify(eventService).validateAndGetEvent(authRequest.request().eventId());
        verify(requestService).initializeRequest(authRequest.request());
        verify(paymentDataAccessService).createPayment(any());
        verify(paymentProcessingService).authorizePayment(any(StripePaymentAuthorizationRequest.class), any(), any(), any());
        verify(paymentDataAccessService).updatePaymentAfterAuthentication(dummyPayment, dummyAuthResponse);
    }

    @Test
    public void testCaptureAndSplitPayment_success() throws StripeException {
        // Arrange
        Amount totalAmount = Amount.of(BigDecimal.valueOf(AMOUNT), CURRENCY_BGN);
        CapturePaymentResponse dummyCaptureResponse = createCapturePaymentResponse(totalAmount, 95);

        PaymentSplitDetails dummySplitDetails = createPaymentSplitDetails();

        when(paymentProcessingService.capturePayment(PAYMENT_INTEND_ID, IDEMPOTENCY_KEY)).thenReturn(dummyCaptureResponse);
        when(paymentDataAccessService.getPaymentSplitData(PAYMENT_INTEND_ID)).thenReturn(dummySplitDetails);

        // Act
        paymentManagementService.captureAndSplitPayment(PAYMENT_INTEND_ID, IDEMPOTENCY_KEY);

        // Assert
        verify(paymentProcessingService).capturePayment(PAYMENT_INTEND_ID, IDEMPOTENCY_KEY);
        verify(paymentDataAccessService).getPaymentSplitData(PAYMENT_INTEND_ID);
        verify(paymentProcessingService).splitPayments(PAYMENT_INTEND_ID, dummySplitDetails, dummyCaptureResponse.totalAmount(), IDEMPOTENCY_KEY);
        verify(partialPaymentService).recordAllocations(dummySplitDetails, dummyCaptureResponse.totalAmount(), dummyCaptureResponse.capturedAmount());
    }
}
