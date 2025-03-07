package com.club_vibe.app_be.users.club.mapper;

import com.club_vibe.app_be.users.club.dto.ClubDTO;
import com.club_vibe.app_be.users.club.entity.ClubEntity;
import org.springframework.stereotype.Component;

@Component
public class ClubMapper {
    public ClubDTO mapClubToDTO(ClubEntity club) {
        return new ClubDTO(
                club.getId(),
                club.getName()
        );
    }
}
