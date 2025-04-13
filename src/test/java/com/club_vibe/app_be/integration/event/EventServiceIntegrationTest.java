package com.club_vibe.app_be.integration.event;

import com.club_vibe.app_be.common.exception.ItemNotFoundException;
import com.club_vibe.app_be.common.util.DefaultPlatformValues;
import com.club_vibe.app_be.events.dto.EventDTO;
import com.club_vibe.app_be.events.entity.EventConditionsEntity;
import com.club_vibe.app_be.events.entity.EventEntity;
import com.club_vibe.app_be.events.entity.RequestSettings;
import com.club_vibe.app_be.events.repository.EventRepository;
import com.club_vibe.app_be.events.service.EventService;
import com.club_vibe.app_be.helpers.EventTestHelper;
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
    private EventService eventService;

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
                .artistPercentage(DefaultPlatformValues.ARTIST_COMMISSION)
                .clubPercentage(DefaultPlatformValues.CLUB_COMMISSION)
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
        event.setActive(false);
        event.setClub(club);
        event.setArtist(artist);
        event.setConditions(EventTestHelper.createDefaultEventConditionsEntity());
        event = eventRepository.save(event);

        EventDTO result = eventService.findEventById(event.getId());
        assertNotNull(result, "Expected an active event for the given club id");
        assertEquals(event.getId(), result.id(), "The returned event id should match the persisted event");
        assertFalse(result.isActive());
        assertEquals(DefaultPlatformValues.ARTIST_COMMISSION, result.artist().percentage());
        assertEquals(DefaultPlatformValues.CLUB_COMMISSION, result.club().percentage());
        assertEquals(event.getStartTime(), result.startTime());
        assertEquals(event.getEndTime(), result.endTime());
    }

    @Test
    public void testFindActiveEventByUserId_NoActiveEvent() {
        assertThrows(ItemNotFoundException.class, () -> eventService.findEventById(1L));
    }
}
