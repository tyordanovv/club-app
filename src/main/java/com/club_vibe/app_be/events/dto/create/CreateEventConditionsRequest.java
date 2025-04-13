package com.club_vibe.app_be.events.dto.create;

/**
 *
 * @param songIsEnabled {@link Boolean}
 * @param greetingIsEnabled {@link Boolean}
 * @param pictureIsEnabled {@link Boolean}
 */
public record CreateEventConditionsRequest (
        Integer maxRequests,
        Boolean songIsEnabled,
        Boolean greetingIsEnabled,
        Boolean pictureIsEnabled
){}
