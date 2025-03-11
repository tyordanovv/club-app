package com.club_vibe.app_be.rabbitmq.consumer;

import com.club_vibe.app_be.rabbitmq.config.RabbitMQConfig;
import com.club_vibe.app_be.rabbitmq.event.ConnectedAccountEvent;
import com.club_vibe.app_be.stripe.accounts.dto.create.CreateConnectedAccountRequest;
import com.club_vibe.app_be.stripe.accounts.service.ConnectedAccountService;
import com.club_vibe.app_be.users.staff.dto.UpdateStripeDetailsRequest;
import com.club_vibe.app_be.users.staff.entity.KycStatus;
import com.club_vibe.app_be.users.staff.service.StaffService;
import com.stripe.exception.StripeException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class StripeConsumer {
    private final StaffService staffService;
    private final ConnectedAccountService connectedAccountService;

    @RabbitListener(queues = RabbitMQConfig.ACCOUNT_QUEUE)
    @Retryable(maxAttempts = 3)
    public void handleConnectedAccountCreation(ConnectedAccountEvent connectedAccountEvent) throws StripeException {
        if (connectedAccountEvent == null || connectedAccountEvent.getEmail() == null) {
            log.error("Received invalid connected account creation event.");
            return;
        }

        log.info("Received create connected account creation event: {}", connectedAccountEvent.getEmail());
        try {
            String connectedAccountId = connectedAccountService.createConnectedAccount(
                    new CreateConnectedAccountRequest(
                            connectedAccountEvent.getCountry(),
                            connectedAccountEvent.getEmail()
                    )
            );
            staffService.updateStripeDetails(
                    new UpdateStripeDetailsRequest(
                            connectedAccountEvent.getEmail(),
                            connectedAccountId,
                            KycStatus.UNVERIFIED
                    )
            );
            log.info("Successfully processed connected account event with id: {}", connectedAccountId);
        } catch (Exception e) {
            log.error("Error processing invitation event: {}", connectedAccountEvent, e);
            throw e;
        }
    }
}
