package com.club_vibe.app_be.rabbitmq.producer;

import com.club_vibe.app_be.rabbitmq.config.RabbitMQConfig;
import com.club_vibe.app_be.rabbitmq.event.ConnectedAccountEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class StripeProducer {
    private final RabbitTemplate rabbitTemplate;

    public void publishConnectedAccountCreationEvent(ConnectedAccountEvent accountEvent) {
        log.info("Publishing account event for email: {}", accountEvent.getEmail());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.APP_EXCHANGE,
                RabbitMQConfig.ACCOUNT_ROUTING_KEY,
                accountEvent
        );
        log.debug("Successfully published account event: {}", accountEvent);
    }
}
