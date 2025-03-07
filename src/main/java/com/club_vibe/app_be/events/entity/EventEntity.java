package com.club_vibe.app_be.events.entity;

import com.club_vibe.app_be.common.enums.InvitationStatus;
import com.club_vibe.app_be.events.dto.EventDTO;
import com.club_vibe.app_be.users.artist.dto.ArtistDTO;
import com.club_vibe.app_be.users.club.dto.ClubDTO;
import com.club_vibe.app_be.users.club.entity.ClubEntity;
import com.club_vibe.app_be.users.artist.entity.ArtistEntity;
import com.club_vibe.app_be.users.request.entity.RequestEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    private boolean isActive = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private ClubEntity club;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dj_id", nullable = false)
    private ArtistEntity dj;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RequestEntity> requests = new ArrayList<>();

    public EventDTO mapToEventResponseDTOWithCustomStatus(InvitationStatus status) {
        return new EventDTO(
                this.getId(),
                this.getStartTime(),
                this.getEndTime(),
                this.isActive(),
                new ClubDTO(
                        this.getClub().getId(),
                        this.getClub().getName()),
                new ArtistDTO(
                        this.getDj().getId(),
                        this.getDj().getStageName()),
                status.name()
        );
    }
}