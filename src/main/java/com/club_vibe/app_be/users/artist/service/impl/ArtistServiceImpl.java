package com.club_vibe.app_be.users.artist.service.impl;

import com.club_vibe.app_be.common.exception.ItemNotFoundException;
import com.club_vibe.app_be.users.artist.dto.ArtistDTO;
import com.club_vibe.app_be.users.artist.mapper.ArtistMapper;
import com.club_vibe.app_be.users.artist.repository.ArtistRepository;
import com.club_vibe.app_be.users.artist.service.ArtistService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ArtistServiceImpl implements ArtistService {

    private final ArtistRepository artistRepository;
    private final ArtistMapper artistMapper;

    @Override
    public List<ArtistDTO> getAllArtists() {
        return artistRepository.findAll()
                .stream()
                .map(artistMapper::mapArtistToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ArtistDTO findById(Long artistId) {
        return artistRepository.findById(artistId)
                .map(artistMapper::mapArtistToDTO)
                .orElseThrow(() -> new ItemNotFoundException(NAME, artistId.toString()));
    }

    @Override
    public ArtistDTO findByEmail(String email) {
        return artistRepository.findByEmail(email)
                .map(artistMapper::mapArtistToDTO)
                .orElseThrow(() -> new ItemNotFoundException(NAME, email));
    }
}
