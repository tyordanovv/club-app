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

    @Query(value = "SELECT ai.id as id, ai.event_id as eventId, e.start_time as startTime, ai.status as status, c.name as clubName " +
            "FROM invitations ai " +
            "JOIN events e ON ai.event_id = e.id " +
            "JOIN clubs c ON ai.club_id = c.id " +
            "WHERE ai.artist_id = :artistId AND ai.status = :status AND e.start_time > :now",
            nativeQuery = true)
    List<PendingInvitationResponse> findPendingFutureInvitations(
            @Param("artistId") Long artistId,
            @Param("status") String status,
            @Param("now") LocalDateTime now
    );

}
