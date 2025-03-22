package com.club_vibe.app_be.stripe.payments.service.impl;

import com.club_vibe.app_be.common.exception.ItemNotFoundException;
import com.club_vibe.app_be.common.util.validator.PercentageValidator;
import com.club_vibe.app_be.request.entity.RequestEntity;
import com.club_vibe.app_be.stripe.payments.dto.CreatePaymentRequest;
import com.club_vibe.app_be.stripe.payments.dto.PaymentSplitDetails;
import com.club_vibe.app_be.stripe.payments.dto.authorize.AuthorizePaymentResponse;
import com.club_vibe.app_be.stripe.payments.entity.PaymentEntity;
import com.club_vibe.app_be.stripe.payments.entity.StripePaymentStatus;
import com.club_vibe.app_be.stripe.payments.repository.PaymentRepository;
import com.club_vibe.app_be.stripe.payments.service.PaymentDataAccessService;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class PaymentDataAccessServiceImpl implements PaymentDataAccessService {
    private final PaymentRepository paymentRepository;
    private final EntityManager entityManager;

    @Override
    public PaymentEntity createPayment(CreatePaymentRequest request) {
        PaymentEntity payment = new PaymentEntity();
        payment.setAmount(request.amount().getValue());
        payment.setCurrency(request.currency().getCurrencyCode());
        payment.setStatus(StripePaymentStatus.CREATED);

        if (request.requestId() != null) {
            payment.setRequest(entityManager.getReference(RequestEntity.class, request.requestId()));
        }

        log.info("Creating new payment with amount: {} {}", request.amount().getValue(), request.currency());
        return paymentRepository.save(payment);
    }

    @Override
    public void updatePaymentAfterAuthentication(PaymentEntity payment, AuthorizePaymentResponse authResponse) {
        payment.setStripePaymentIntentId(authResponse.paymentIntentId());
        payment.setStatus(authResponse.paymentStatus());
        paymentRepository.save(payment);
    }

    @Override
    public PaymentSplitDetails getPaymentSplitData(String paymentIntentId) {
        PaymentSplitDetails splitDetails = paymentRepository.findPaymentSplitDataByPaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new ItemNotFoundException(NAME, paymentIntentId));

        if (StringUtils.isBlank(splitDetails.clubConnectedAccountId())) {
            throw new IllegalStateException("Club connected account ID is missing for PaymentIntentId: " + paymentIntentId);
        }
        if (StringUtils.isBlank(splitDetails.artistConnectedAccountId())) {
            throw new IllegalStateException("Artist connected account ID is missing for PaymentIntentId: " + paymentIntentId);
        }

        PercentageValidator.validateClubPercentage(splitDetails.clubPercentage());
        PercentageValidator.validateArtistPercentage(splitDetails.artistPercentage());
        PercentageValidator.validateTotalPercentage(splitDetails.clubPercentage(), splitDetails.artistPercentage());

        log.info("Retrieved valid split data for PaymentIntentId {}", paymentIntentId);
        return splitDetails;
    }
}
