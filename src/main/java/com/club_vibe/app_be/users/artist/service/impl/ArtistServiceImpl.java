package com.club_vibe.app_be.users.artist.service.impl;

import com.club_vibe.app_be.common.exception.ItemNotFoundException;
import com.club_vibe.app_be.users.artist.dto.ArtistDTO;
import com.club_vibe.app_be.users.artist.mapper.ArtistMapper;
import com.club_vibe.app_be.users.artist.repository.ArtistRepository;
import com.club_vibe.app_be.users.artist.service.ArtistService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ArtistServiceImpl implements ArtistService {
    private final ArtistRepository artistRepository;
    private final ArtistMapper artistMapper;

    @Override
    public List<ArtistDTO> getAllArtists() {
        return artistRepository.findAll().stream()
                .map(artistMapper::mapArtistToDTO)
                .toList();
    }

    @Override
    public ArtistDTO findById(Long artistId) {
        return artistRepository.findById(artistId)
                .map(artistMapper::mapArtistToDTO)
                .orElseThrow(() -> new ItemNotFoundException(artistId.toString()));
    }

    @Override
    public ArtistDTO findByEmail(String email) {
        return artistRepository.findByEmail(email)
                .map(artistMapper::mapArtistToDTO)
                .orElseThrow(() -> new ItemNotFoundException(email));
    }
}

