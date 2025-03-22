package com.club_vibe.app_be.stripe.payments.repository;

import com.club_vibe.app_be.stripe.balance.dto.artist.ArtistPaymentDto;
import com.club_vibe.app_be.stripe.balance.dto.club.ClubEarningDto;
import com.club_vibe.app_be.stripe.payments.entity.partial.PartialPaymentEntity;
import com.club_vibe.app_be.stripe.payments.entity.partial.PaymentAllocationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PartialPaymentRepository extends JpaRepository<PartialPaymentEntity, String> {

    /**
     * Find artist payments by month
     * This query returns data in the same structure as the original but uses constructor expression
     * to create DTOs instead of Object arrays
     */
    @Query("SELECT new com.club_vibe.app_be.stripe.balance.dto.artist.ArtistPaymentDto(" +
            "pp.id, e.startTime, pp.amount, pp.currency, pp.allocationDate, r.title, p.status) " +
            "FROM PartialPaymentEntity pp " +
            "JOIN pp.payment p " +
            "JOIN p.request r " +
            "JOIN r.event e " +
            "JOIN e.artist a " +
            "WHERE a.stripeDetails.accountId = :stripeAccountId " +
            "AND pp.type = :paymentType " +
            "AND pp.allocationDate >= :startDate " +
            "ORDER BY pp.allocationDate DESC")
    List<ArtistPaymentDto> findArtistPaymentsByMonth(
            @Param("stripeAccountId") String stripeAccountId,
            @Param("paymentType") PaymentAllocationType paymentType,
            @Param("startDate") LocalDateTime startDate);

    /**
     * Find club earnings by month
     * This query returns data in the same structure as the original but uses constructor expression
     * to create DTOs instead of Object arrays
     */
    @Query("SELECT new com.club_vibe.app_be.stripe.balance.dto.club.ClubEarningDto(" +
            "e.id, e.startTime, SUM(pp.amount), pp.currency, CAST(COUNT(pp.id) AS integer), MAX(pp.allocationDate)) " +
            "FROM PartialPaymentEntity pp " +
            "JOIN pp.payment p " +
            "JOIN p.request r " +
            "JOIN r.event e " +
            "JOIN e.club c " +
            "WHERE c.stripeDetails.accountId = :stripeAccountId " +
            "AND pp.type = :paymentType " +
            "AND pp.allocationDate >= :startDate " +
            "GROUP BY e.id, e.startTime, pp.currency " +
            "ORDER BY e.startTime DESC")
    List<ClubEarningDto> findClubEarningsByMonth(
            @Param("stripeAccountId") String stripeAccountId,
            @Param("paymentType") PaymentAllocationType paymentType,
            @Param("startDate") LocalDateTime startDate);
}