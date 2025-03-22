package com.club_vibe.app_be.events.entity;

import com.club_vibe.app_be.users.club.entity.ClubEntity;
import com.club_vibe.app_be.users.artist.entity.ArtistEntity;
import com.club_vibe.app_be.request.entity.RequestEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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

    @Column(nullable = false, name = "start_time")
    private LocalDateTime startTime;

    @Column(nullable = false, name = "end_time")
    private LocalDateTime endTime;

    @Column(nullable = false, name = "artist_percentage")
    private BigDecimal artistPercentage;

    @Column(nullable = false, name = "club_percentage")
    private BigDecimal clubPercentage;

    @Column(nullable = false, name = "is_active")
    private boolean isActive = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "club_id")
    private ClubEntity club;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "artist_id")
    private ArtistEntity artist;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RequestEntity> requests = new ArrayList<>();
}