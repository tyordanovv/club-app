package com.club_vibe.app_be.rabbitmq.consumer;

import com.club_vibe.app_be.events.service.EventService;
import com.club_vibe.app_be.rabbitmq.config.RabbitMQConfig;
import com.club_vibe.app_be.rabbitmq.event.ActivationEvent;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class EventConsumer {

    private final EventService eventService;

    @RabbitListener(queues = RabbitMQConfig.EVENT_QUEUE)
    @Retryable(maxAttempts = 3)
    public void handleInvitationEvent(ActivationEvent activationEvent) {
        if (activationEvent == null || activationEvent.getInvitationId() == null) {
            log.error("Received invalid activation event");
            return;
        }

        log.info("Received activation event for invitationId: {}", activationEvent.getInvitationId());
        try {
            eventService.activateEvent(activationEvent.getInvitationId());
            log.info("Successfully processed invitation event for id: {}", activationEvent.getInvitationId());
        } catch (Exception e) {
            log.error("Error processing invitation event: {}", activationEvent, e);
            throw e;
        }
    }
}