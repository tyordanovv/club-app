package com.club_vibe.app_be.users.club.service.impl;

import com.club_vibe.app_be.common.exception.ItemNotFoundException;
import com.club_vibe.app_be.users.club.dto.ClubDTO;
import com.club_vibe.app_be.users.club.mapper.ClubMapper;
import com.club_vibe.app_be.users.club.repository.ClubRepository;
import com.club_vibe.app_be.users.club.service.ClubService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ClubServiceImpl implements ClubService {

    private final ClubRepository clubRepository;
    private final ClubMapper clubMapper;

    @Override
    public ClubDTO findByEmail(String clubEmail) {
        return clubRepository.findByEmail(clubEmail)
                .map(clubMapper::mapClubToDTO)
                .orElseThrow(() -> new ItemNotFoundException(NAME, clubEmail));
    }
}
