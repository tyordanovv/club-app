package com.club_vibe.app_be.rabbitmq.producer;

import com.club_vibe.app_be.rabbitmq.config.RabbitMQConfig;
import com.club_vibe.app_be.rabbitmq.event.ActivationEvent;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class EventProducer {
    private final RabbitTemplate rabbitTemplate;

    public void publishActivationEvent(ActivationEvent activationEvent) {
        log.info("Publishing activation event for invitationId: {}", activationEvent.getInvitationId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.APP_EXCHANGE,
                RabbitMQConfig.EVENT_ACTIVATION_ROUTING_KEY,
                activationEvent
        );
        log.debug("Successfully published activation event: {}", activationEvent);
    }
}
