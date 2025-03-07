package com.club_vibe.app_be.users.artist.repository;

import com.club_vibe.app_be.common.enums.InvitationStatus;
import com.club_vibe.app_be.users.artist.dto.PendingInvitationResponse;
import com.club_vibe.app_be.users.artist.entity.ArtistInvitationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ArtistInvitationRepository extends JpaRepository<ArtistInvitationEntity, Long> {

    @Query("""
                SELECT new com.club_vibe.app_be.staff.artist.dto.PendingInvitationDTO(
                ai.id,
                ai.eventId,
                e.startTime,
                ai.status,
                c.name
                )
            FROM ArtistInvitationEntity ai
                JOIN EventEntity e ON ai.eventId = e.id
                JOIN ClubEntity c ON ai.club.id = c.id
            WHERE ai.artist.id = :artistId
                AND ai.status = :status
                AND e.startTime > :now
            """)
    List<PendingInvitationResponse> findPendingFutureInvitations(
            @Param("artistId") Long artistId,
            @Param("status") InvitationStatus status,
            @Param("now") LocalDateTime now
    );
}
