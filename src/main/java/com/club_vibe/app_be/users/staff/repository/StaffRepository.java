package com.club_vibe.app_be.users.staff.repository;

import com.club_vibe.app_be.users.staff.entity.StaffEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<StaffEntity, Long> {
    Optional<StaffEntity> findByEmail(String email);

    void deleteByEmail(String email);

    @Query("SELECT s FROM StaffEntity s WHERE s.stripeDetails.accountId = :accountId")
    Optional<StaffEntity> findByStripeAccountId(@Param("accountId") String accountId);
}