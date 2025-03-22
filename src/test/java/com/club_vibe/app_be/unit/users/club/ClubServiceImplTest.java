package com.club_vibe.app_be.unit.users.club;

import com.club_vibe.app_be.common.exception.ItemNotFoundException;
import com.club_vibe.app_be.users.club.dto.ClubDTO;
import com.club_vibe.app_be.users.club.entity.ClubEntity;
import com.club_vibe.app_be.users.club.mapper.ClubMapper;
import com.club_vibe.app_be.users.club.repository.ClubRepository;
import com.club_vibe.app_be.users.club.service.impl.ClubServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClubServiceImplTest {

    @Mock
    private ClubRepository clubRepository;

    @Mock
    private ClubMapper clubMapper;

    private ClubServiceImpl clubService;

    @BeforeEach
    public void setup() {
        clubService = new ClubServiceImpl(clubRepository, clubMapper);
    }

    @Test
    void findByEmail_shouldReturnClubDTO_whenClubExists() {
        String email = "club@example.com";
        ClubEntity clubEntity = new ClubEntity();
        ClubDTO clubDTO = new ClubDTO(1L, "CLUB_NAME");

        when(clubRepository.findByEmail(email)).thenReturn(Optional.of(clubEntity));
        when(clubMapper.mapClubToDTO(clubEntity)).thenReturn(clubDTO);

        ClubDTO result = clubService.findByEmail(email);

        assertEquals(clubDTO, result);
    }

    @Test
    void findByEmail_shouldThrowItemNotFoundException_whenClubDoesNotExist() {
        String email = "notfound@example.com";
        when(clubRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> clubService.findByEmail(email));
    }
}