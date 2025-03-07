package com.club_vibe.app_be.events.dto;

import com.club_vibe.app_be.users.artist.dto.ArtistDTO;
import com.club_vibe.app_be.users.club.dto.ClubDTO;

import java.time.LocalDateTime;

public record EventDTO(
        Long id,
        LocalDateTime startTime,
        LocalDateTime endTime,
        boolean isActive,
        ClubDTO club,
        ArtistDTO artist,
        String invitationStatus
) {}
