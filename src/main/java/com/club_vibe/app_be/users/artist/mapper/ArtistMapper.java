package com.club_vibe.app_be.users.artist.mapper;

import com.club_vibe.app_be.users.artist.dto.ArtistDTO;
import com.club_vibe.app_be.users.artist.dto.ArtistInvitationDTO;
import com.club_vibe.app_be.users.artist.entity.ArtistEntity;
import com.club_vibe.app_be.users.artist.entity.ArtistInvitationEntity;
import org.springframework.stereotype.Component;

@Component
public class ArtistMapper {
    public ArtistDTO mapArtistToDTO(ArtistEntity artist) {
        return new ArtistDTO(
                artist.getId(),
                artist.getStageName()
        );
    }

    public ArtistInvitationDTO mapArtistInvitationToDTO(ArtistInvitationEntity artistInvitation) {
        return new ArtistInvitationDTO(
                artistInvitation.getId(),
                artistInvitation.getClub().getId(),
                artistInvitation.getArtist().getId(),
                artistInvitation.getStatus()
        );
    }
}
