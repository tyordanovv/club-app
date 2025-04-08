package com.club_vibe.app_be.integration.event;

import com.club_vibe.app_be.events.entity.EventEntity;
import com.club_vibe.app_be.events.repository.EventRepository;
import com.club_vibe.app_be.events.service.EventService;
import com.club_vibe.app_be.users.artist.entity.ArtistEntity;
import com.club_vibe.app_be.users.artist.repository.ArtistRepository;
import com.club_vibe.app_be.users.club.entity.ClubEntity;
import com.club_vibe.app_be.users.club.repository.ClubRepository;
import com.club_vibe.app_be.users.staff.role.StaffRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class EventServiceIntegrationTest {
    @Autowired
    private EventService eventService; // Service under test

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private ArtistRepository artistRepository;

    private ClubEntity club;
    private ArtistEntity artist;

    @BeforeEach
    public void setUp() {
        club = ClubEntity.builder()
                .email("club@example.com")
                .password("password")
                .role(StaffRole.CLUB)
                .name("Test Club")
                .build();
        club = clubRepository.save(club);

        artist = ArtistEntity.builder()
                .email("artist@example.com")
                .password("password")
                .role(StaffRole.ARTIST)
                .stageName("Test Artist")
                .build();
        artist = artistRepository.save(artist);
    }

    @Test
    public void testFindActiveEventByUserId_WithActiveEvent() {
        LocalDateTime now = LocalDateTime.now();

        // Create an active event associated with the club and artist.
        EventEntity event = new EventEntity();
        event.setStartTime(now.minusHours(1));
        event.setEndTime(now.plusHours(1));
        event.setActive(true);
        event.setClub(club);
        event.setClubPercentage(BigDecimal.valueOf(20));
        event.setArtist(artist);
        event.setArtistPercentage(BigDecimal.valueOf(60));
        event = eventRepository.save(event);

        Optional<Long> result = eventService.findActiveEventByUserId(club.getId());
        assertTrue(result.isPresent(), "Expected an active event for the given club id");
        assertEquals(event.getId(), result.get(), "The returned event id should match the persisted event");
    }

    @Test
    public void testFindActiveEventByUserId_NoActiveEvent() {
        Optional<Long> result = eventService.findActiveEventByUserId(club.getId());
        assertFalse(result.isPresent(), "Expected no active event for the given club id");
    }
}
