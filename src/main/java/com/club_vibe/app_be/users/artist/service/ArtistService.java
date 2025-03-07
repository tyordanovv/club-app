package com.club_vibe.app_be.users.artist.service;

import com.club_vibe.app_be.users.artist.dto.ArtistDTO;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public interface ArtistService {

    /**
     *
     * @return
     */
    List<ArtistDTO> getAllArtists();

    /**
     *
     * @param artistId
     * @return
     */
    ArtistDTO findById(@NotBlank Long artistId);

    /**
     *
     * @param email
     * @return
     */
    ArtistDTO findByEmail(String email);
}
