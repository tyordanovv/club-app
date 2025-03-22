package com.club_vibe.app_be.unit.stripe.payment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import com.club_vibe.app_be.common.util.Amount;
import com.club_vibe.app_be.stripe.payments.dto.PaymentSplitDetails;
import com.club_vibe.app_be.stripe.payments.entity.PaymentEntity;
import com.club_vibe.app_be.stripe.payments.entity.partial.PartialPaymentEntity;
import com.club_vibe.app_be.stripe.payments.entity.partial.PaymentAllocationType;
import com.club_vibe.app_be.stripe.payments.repository.PartialPaymentRepository;
import com.club_vibe.app_be.stripe.payments.service.impl.PartialPaymentServiceImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PartialPaymentServiceImplTest {

    @Mock
    private PartialPaymentRepository partialPaymentRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private PartialPaymentServiceImpl partialPaymentService;

    private PaymentSplitDetails splitDetails;
    private Amount totalAmount;
    private Amount capturedAmount;
    private PaymentEntity paymentEntity;

    private static final BigDecimal TOTAL_AMOUNT = BigDecimal.valueOf(200.0);
    private static final BigDecimal CAPTURED_AMOUNT = BigDecimal.valueOf(196.3);
    private static final BigDecimal CLUB_PERCENTAGE = BigDecimal.valueOf(60); // 120
    private static final BigDecimal ARTIST_PERCENTAGE = BigDecimal.valueOf(20); // 40
    private static final Currency CURRENCY = Currency.getInstance("BGN");
    private static final Long PAYMENT_ID = 123L;

    @BeforeEach
    void setUp() {
        // Prepare a PaymentSplitDetails with test values:
        splitDetails = new PaymentSplitDetails(
                PAYMENT_ID,
                "club_acc",
                "artist_acc",
                CLUB_PERCENTAGE,
                ARTIST_PERCENTAGE);

        totalAmount = spy(Amount.of(TOTAL_AMOUNT, CURRENCY));
        capturedAmount = spy(Amount.of(CAPTURED_AMOUNT, CURRENCY));

        // Dummy PaymentEntity returned by the entity manager
        paymentEntity = new PaymentEntity();
        when(entityManager.getReference(PaymentEntity.class, PAYMENT_ID)).thenReturn(paymentEntity);
    }

    @Test
    void recordAllocations_shouldSaveFourAllocations() {
        Amount clubAmount = totalAmount.calculatePercentage(CLUB_PERCENTAGE);
        Amount artistAmount = totalAmount.calculatePercentage(ARTIST_PERCENTAGE);
        Amount feeAmount = Amount.of(
                totalAmount.getValue()
                        .subtract(capturedAmount.getValue()),
                CURRENCY);
        Amount platformAmount = Amount.of(
                capturedAmount.getValue()
                        .subtract(artistAmount.getValue())
                        .subtract(clubAmount.getValue()),
                CURRENCY);

        // Act
        partialPaymentService.recordAllocations(splitDetails, totalAmount, capturedAmount);

        // Capture the allocations saved via repository.saveAll()
        ArgumentCaptor<List<PartialPaymentEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(partialPaymentRepository).saveAll(captor.capture());
        List<PartialPaymentEntity> allocations = captor.getValue();

        // Assert that four allocations were saved
        assertEquals(4, allocations.size(), "Four allocations should be created");

        // Validate each allocation by type
        PartialPaymentEntity clubAllocation = allocations.stream()
                .filter(a -> a.getType() == PaymentAllocationType.CLUB)
                .findFirst()
                .orElseThrow();
        assertEquals(clubAmount.getValue(), clubAllocation.getAmount());
        assertEquals("Club payout", clubAllocation.getDescription());
        assertEquals(paymentEntity, clubAllocation.getPayment());

        PartialPaymentEntity artistAllocation = allocations.stream()
                .filter(a -> a.getType() == PaymentAllocationType.ARTIST)
                .findFirst()
                .orElseThrow();
        assertEquals(artistAmount.getValue(), artistAllocation.getAmount());
        assertEquals("Artist payout", artistAllocation.getDescription());
        assertEquals(paymentEntity, artistAllocation.getPayment());

        PartialPaymentEntity platformAllocation = allocations.stream()
                .filter(a -> a.getType() == PaymentAllocationType.PLATFORM)
                .findFirst()
                .orElseThrow();

        assertEquals(platformAmount.getValue(), platformAllocation.getAmount());
        assertEquals("Platform retention", platformAllocation.getDescription());
        assertEquals(paymentEntity, platformAllocation.getPayment());

        PartialPaymentEntity feeAllocation = allocations.stream()
                .filter(a -> a.getType() == PaymentAllocationType.FEE)
                .findFirst()
                .orElseThrow();
        assertEquals(feeAmount.getValue(), feeAllocation.getAmount());
        assertEquals("Processing fee", feeAllocation.getDescription());
        assertEquals(paymentEntity, feeAllocation.getPayment());
    }
}