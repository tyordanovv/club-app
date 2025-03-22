package com.club_vibe.app_be.events;

import com.club_vibe.app_be.common.enums.InvitationStatus;
import com.club_vibe.app_be.events.dto.EventDTO;
import com.club_vibe.app_be.events.dto.create.CreateEventResponse;
import com.club_vibe.app_be.events.entity.EventEntity;
import com.club_vibe.app_be.users.artist.dto.ArtistDTO;
import com.club_vibe.app_be.users.artist.dto.ArtistDetails;
import com.club_vibe.app_be.users.club.dto.ClubDTO;
import com.club_vibe.app_be.users.club.dto.ClubDetails;
import com.club_vibe.app_be.users.staff.entity.StripeDetails;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {
    public EventDTO toEventDTO(EventEntity event) {
        return new EventDTO(
                event.getId(),
                event.getStartTime(),
                event.getEndTime(),
                event.isActive(),
                new ClubDetails(
                        event.getClub().getId(),
                        event.getClub().getName(),
                        event.getClub().getStripeDetails(),
                        event.getClubPercentage()
                        ),
                new ArtistDetails(
                        event.getArtist().getId(),
                        event.getArtist().getStageName(),
                        event.getArtist().getStripeDetails(),
                        event.getArtistPercentage()
                        )
        );
    }

    public CreateEventResponse toCreateEventResponse(EventEntity event, InvitationStatus status) {
        return new CreateEventResponse(
                toEventDTO(event),
                status
        );
    }
}
