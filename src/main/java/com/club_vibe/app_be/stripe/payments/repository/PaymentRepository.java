package com.club_vibe.app_be.stripe.payments.repository;

import com.club_vibe.app_be.stripe.payments.dto.PaymentSplitDetails;
import com.club_vibe.app_be.stripe.payments.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    @Query("SELECT new com.club_vibe.app_be.stripe.payments.dto.PaymentSplitDetails" +
            "(p.id, COALESCE(club.stripeDetails.accountId, ''), " +
            "COALESCE(artist.stripeDetails.accountId, ''), " +
            "conditions.clubPercentage, " +
            "conditions.artistPercentage) " +
            "FROM PaymentEntity p " +
            "JOIN p.request r " +
            "JOIN r.event event " +
            "JOIN event.conditions conditions " +
            "JOIN event.club club " +
            "JOIN event.artist artist " +
            "WHERE p.stripePaymentIntentId = :paymentIntentId")
    Optional<PaymentSplitDetails> findPaymentSplitDataByPaymentIntentId(@Param("paymentIntentId") String paymentIntentId);
}
