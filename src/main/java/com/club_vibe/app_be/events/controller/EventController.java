package com.club_vibe.app_be.events.controller;

import com.club_vibe.app_be.events.dto.create.CreateEventRequest;
import com.club_vibe.app_be.events.dto.create.CreateEventResponse;
import com.club_vibe.app_be.events.service.EventService;
import com.club_vibe.app_be.users.auth.service.CurrentUserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/events")
public class EventController {
    private final EventService eventService;
    private final CurrentUserService currentUserService;

    /**
     * Endpoint to create an event and invite an artist
     *
     * @param request event creation request with artist invitation
     * @return created event details
     */
    @PostMapping
    @PreAuthorize("hasRole('CLUB')")
    public ResponseEntity<CreateEventResponse> createEvent(
            @Valid @RequestBody CreateEventRequest request
    ) {
        Long clubId = currentUserService.getCurrentUserPlatformId();
        CreateEventResponse createdEvent = eventService.createEventAndInviteArtist(request, clubId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }
}
