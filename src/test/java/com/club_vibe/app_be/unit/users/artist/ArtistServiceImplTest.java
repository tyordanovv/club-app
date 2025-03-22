package com.club_vibe.app_be.unit.users.artist;

import com.club_vibe.app_be.common.exception.ItemNotFoundException;
import com.club_vibe.app_be.users.artist.dto.ArtistDTO;
import com.club_vibe.app_be.users.artist.entity.ArtistEntity;
import com.club_vibe.app_be.users.artist.mapper.ArtistMapper;
import com.club_vibe.app_be.users.artist.repository.ArtistRepository;
import com.club_vibe.app_be.users.artist.service.impl.ArtistServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ArtistServiceImplTest {

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private ArtistMapper artistMapper;

    private ArtistServiceImpl artistService;

    @BeforeEach
    public void setup() {
        artistService = new ArtistServiceImpl(artistRepository, artistMapper);
    }

    @Test
    void getAllArtists_shouldReturnMappedArtists() {
        ArtistEntity entity1 = new ArtistEntity();
        ArtistEntity entity2 = new ArtistEntity();
        List<ArtistEntity> artists = List.of(entity1, entity2);
        ArtistDTO dto1 = new ArtistDTO(1L, "NAME1");
        ArtistDTO dto2 = new ArtistDTO(2L, "NAME2");

        when(artistRepository.findAll()).thenReturn(artists);
        when(artistMapper.mapArtistToDTO(entity1)).thenReturn(dto1);
        when(artistMapper.mapArtistToDTO(entity2)).thenReturn(dto2);

        List<ArtistDTO> result = artistService.getAllArtists();

        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of(dto1, dto2)));
    }

    @Test
    void findById_shouldReturnArtistDTO_whenFound() {
        Long id = 1L;
        ArtistEntity entity = new ArtistEntity();
        ArtistDTO dto = new ArtistDTO(1L, "NAME1");

        when(artistRepository.findById(id)).thenReturn(Optional.of(entity));
        when(artistMapper.mapArtistToDTO(entity)).thenReturn(dto);

        ArtistDTO result = artistService.findById(id);

        assertEquals(dto, result);
    }

    @Test
    void findById_shouldThrowException_whenNotFound() {
        Long id = 1L;
        when(artistRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> artistService.findById(id));
    }

    @Test
    void findByEmail_shouldReturnArtistDTO_whenFound() {
        String email = "artist@example.com";
        ArtistEntity entity = new ArtistEntity();
        ArtistDTO dto = new ArtistDTO(1L, "NAME1");

        when(artistRepository.findByEmail(email)).thenReturn(Optional.of(entity));
        when(artistMapper.mapArtistToDTO(entity)).thenReturn(dto);

        ArtistDTO result = artistService.findByEmail(email);

        assertEquals(dto, result);
    }

    @Test
    void findByEmail_shouldThrowException_whenNotFound() {
        String email = "notfound@example.com";
        when(artistRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> artistService.findByEmail(email));
    }
}