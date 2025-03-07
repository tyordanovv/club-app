package com.club_vibe.app_be.users.artist.controller;

import com.club_vibe.app_be.users.artist.dto.ArtistDTO;
import com.club_vibe.app_be.users.artist.service.impl.ArtistServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/artists")
public class ArtistController {
    private final ArtistServiceImpl artistService;

    /**
     * Endpoint to fetch all artists for club to select from
     *
     * @return list of all artists
     */
    @GetMapping()
    @PreAuthorize("hasRole('CLUB')")
    public ResponseEntity<List<ArtistDTO>> getAllArtists() {
        List<ArtistDTO> artists = artistService.getAllArtists();
        return ResponseEntity.ok(artists);
    }
}
