package com.club_vibe.app_be.rabbitmq.event;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ActivationEvent {
    private Long invitationId;
}