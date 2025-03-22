package com.club_vibe.app_be.events.dto;

import com.club_vibe.app_be.users.artist.dto.ArtistDetails;
import com.club_vibe.app_be.users.club.dto.ClubDetails;

import java.time.LocalDateTime;

public record EventDTO(
        Long id,
        LocalDateTime startTime,
        LocalDateTime endTime,
        boolean isActive,
        ClubDetails club,
        ArtistDetails artist
) {}
