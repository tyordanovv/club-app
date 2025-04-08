package com.club_vibe.app_be.request.repository;

import com.club_vibe.app_be.events.dto.RequestStatus;
import com.club_vibe.app_be.request.entity.RequestEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository extends JpaRepository<RequestEntity, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE RequestEntity r SET r.status = :status WHERE r.id = :requestId")
    int updateStatus(@Param("requestId") Long requestId, @Param("status") RequestStatus status);

}
