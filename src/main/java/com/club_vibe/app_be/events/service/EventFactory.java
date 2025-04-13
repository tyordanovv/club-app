package com.club_vibe.app_be.events.service;

import com.club_vibe.app_be.common.util.DefaultPlatformValues;
import com.club_vibe.app_be.events.dto.create.CreateEventConditionsRequest;
import com.club_vibe.app_be.events.entity.EventConditionsEntity;
import com.club_vibe.app_be.events.entity.RequestSettings;
import com.club_vibe.app_be.users.club.dto.ClubArtistPercentage;

public abstract class EventFactory {
    public static EventConditionsEntity createConditions(
            CreateEventConditionsRequest request, ClubArtistPercentage percentages) {
        return EventConditionsEntity.builder()
                .artistPercentage(percentages.artistPercentage())
                .clubPercentage(percentages.clubPercentage())
                .songRequestSettings(createDefaultSongRequestSettings(request.songIsEnabled()))
                .greetingRequestSettings(createDefaultGreetingRequestSettings(request.greetingIsEnabled()))
                .pictureRequestSettings(createDefaultPictureRequestSettings(request.pictureIsEnabled()))
                .maxRequests(request.maxRequests() == null ? DefaultPlatformValues.DEFAULT_MAX_REQUESTS : request.maxRequests())
                .build();
    }

    private static RequestSettings createDefaultSongRequestSettings(Boolean isEnabled) {
        return RequestSettings.builder()
                .enabled(isEnabled)
                .minPrice(DefaultPlatformValues.MIN_PRICE_SONG_REQUESTS)
                .build();
    }

    private static RequestSettings createDefaultGreetingRequestSettings(Boolean isEnabled) {
        return RequestSettings.builder()
                .enabled(isEnabled)
                .minPrice(DefaultPlatformValues.MIN_PRICE_GREETING_REQUESTS)
                .build();
    }

    private static RequestSettings createDefaultPictureRequestSettings(Boolean isEnabled) {
        return RequestSettings.builder()
                .enabled(isEnabled)
                .minPrice(DefaultPlatformValues.MIN_PRICE_PICTURE_REQUESTS)
                .build();
    }
}