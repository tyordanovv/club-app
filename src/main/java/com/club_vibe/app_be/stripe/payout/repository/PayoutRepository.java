package com.club_vibe.app_be.stripe.payout.repository;

import com.club_vibe.app_be.common.enums.PayoutStatus;
import com.club_vibe.app_be.stripe.payout.entity.PayoutEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayoutRepository extends JpaRepository<PayoutEntity, Long> {

    /**
     * Find payouts by staff ID
     * @param staffId
     * @return
     */
    List<PayoutEntity> findByStaffIdOrderByIdDesc(Long staffId);

    /**
     * Find payouts by status
     * @param status
     * @return
     */
    List<PayoutEntity> findByStatus(PayoutStatus status);

    /**
     *
     * @param stripePayoutId
     * @return
     */
    Optional<PayoutEntity> findByStripePayoutId(String stripePayoutId);
}
