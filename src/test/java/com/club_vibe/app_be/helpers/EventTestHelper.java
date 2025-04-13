package com.club_vibe.app_be.helpers;

import com.club_vibe.app_be.events.dto.create.CreateEventConditionsRequest;
import com.club_vibe.app_be.events.dto.create.CreateEventRequest;
import com.club_vibe.app_be.events.entity.EventConditionsEntity;
import com.club_vibe.app_be.events.entity.RequestSettings;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public abstract class EventTestHelper {
    public static EventConditionsEntity createDefaultEventConditionsEntity() {
        return EventConditionsEntity.builder()
                .artistPercentage(BigDecimal.valueOf(75))
                .clubPercentage(BigDecimal.valueOf(10))
                .maxRequests(15)
                .greetingRequestSettings(
                        RequestSettings.builder()
                                .enabled(true)
                                .minPrice(BigDecimal.valueOf(20))
                                .build())
                .songRequestSettings(
                        RequestSettings.builder()
                                .enabled(true)
                                .minPrice(BigDecimal.valueOf(50))
                                .build())
                .pictureRequestSettings(
                        RequestSettings.builder()
                                .enabled(false)
                                .build())
                .build();
    }

    public static CreateEventRequest createEventRequest() {
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = startTime.plusHours(2);
        return new CreateEventRequest(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusHours(2),
                3L,
                createEventConditionsRequest()
        );
    }

    public static CreateEventConditionsRequest createEventConditionsRequest() {
        return new CreateEventConditionsRequest(
                Boolean.TRUE,
                Boolean.TRUE,
                Boolean.FALSE
        );
    }
}
