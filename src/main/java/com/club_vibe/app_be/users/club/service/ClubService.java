package com.club_vibe.app_be.users.club.service;

import com.club_vibe.app_be.users.club.dto.ClubDTO;

public interface ClubService {
    /**
     *
     * @param clubEmail
     * @return
     */
    ClubDTO findByEmail(String clubEmail);
}
