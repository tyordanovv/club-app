package com.club_vibe.app_be.events.repository;

import com.club_vibe.app_be.events.entity.EventEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE EventEntity e SET e.isActive = :status WHERE e.id = :id")
    void updateEventStatus(@Param("id") Long id,
                           @Param("status") boolean status);

    @Query("SELECT e FROM EventEntity e " +
            "WHERE e.isActive = true " +
            "AND (:now BETWEEN e.startTime AND e.endTime) " +
            "AND (e.club.id = :staffId OR e.artist.id = :staffId)")
    Optional<EventEntity> findActiveEventByStaffId(@Param("staffId") Long staffId,
                                                   @Param("now") LocalDateTime now);
}
