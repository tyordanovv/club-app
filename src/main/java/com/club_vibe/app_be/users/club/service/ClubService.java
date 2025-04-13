package com.club_vibe.app_be.users.club.service;

import com.club_vibe.app_be.users.club.dto.ClubArtistPercentage;
import com.club_vibe.app_be.users.club.dto.ClubDTO;

public interface ClubService {
    String NAME = "Club";

    /**
     *
     * @param clubEmail
     * @return
     */
    ClubDTO findByEmail(String clubEmail);

    /**
     *
     * @param clubId {@link Long}
     * @return {@link ClubArtistPercentage}
     */
    ClubArtistPercentage findClubArtistPercentage(Long clubId);
}
