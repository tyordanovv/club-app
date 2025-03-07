package com.club_vibe.app_be.users.artist.entity;


import com.club_vibe.app_be.common.enums.InvitationStatus;
import com.club_vibe.app_be.users.club.entity.ClubEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "invitations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistInvitationEntity {
    @Id
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true)
    private Long eventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private ClubEntity club;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private ArtistEntity artist;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime respondedAt;

    private String responseMessage;
}
