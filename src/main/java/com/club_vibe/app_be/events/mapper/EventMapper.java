package com.club_vibe.app_be.events.mapper;

import com.club_vibe.app_be.common.enums.InvitationStatus;
import com.club_vibe.app_be.events.dto.EventDTO;
import com.club_vibe.app_be.events.dto.EventRequest;
import com.club_vibe.app_be.events.dto.create.CreateEventResponse;
import com.club_vibe.app_be.events.entity.EventEntity;
import com.club_vibe.app_be.request.dto.RequestDto;
import com.club_vibe.app_be.stripe.payments.entity.PaymentEntity;
import com.club_vibe.app_be.users.artist.dto.ArtistDetails;
import com.club_vibe.app_be.users.club.dto.ClubDetails;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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
                        event.getConditions().getClubPercentage()
                        ),
                new ArtistDetails(
                        event.getArtist().getId(),
                        event.getArtist().getStageName(),
                        event.getArtist().getStripeDetails(),
                        event.getConditions().getArtistPercentage()
                        )

        );
    }

    public CreateEventResponse toCreateEventResponse(EventEntity event, InvitationStatus status) {
        return new CreateEventResponse(
                toEventDTO(event),
                status
        );
    }

    public List<EventRequest> toEventRequests(EventEntity eventEntity) {
        return eventEntity.getRequests().stream()
                .map(request -> {
                    PaymentEntity payment = request.getPayment();
                    String paymentIntentId = payment != null ? payment.getStripePaymentIntentId() : null;

                    RequestDto requestDto = new RequestDto(
                            request.getId(),
                            request.getType(),
                            request.getTitle(),
                            request.getMessage(),
                            request.getStatus()
                    );
                    return new EventRequest(paymentIntentId, requestDto);
                })
                .collect(Collectors.toList());
    }
}
