package com.club_vibe.app_be.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    public static final String APP_EXCHANGE = "event.exchange";
    public static final String EVENT_QUEUE = "event.queue";
    public static final String EVENT_ACTIVATION_ROUTING_KEY = "event.routing.key";

    public static final String ACCOUNT_QUEUE = "account.queue";
    public static final String ACCOUNT_ROUTING_KEY = "account.routing.key";

    public static final String DLX_EXCHANGE = "dead.letter.exchange";
    public static final String DLQ = "dead.letter.queue";

    @Bean
    public TopicExchange eventExchange() {
        return new TopicExchange(APP_EXCHANGE);
    }

    @Bean
    public Queue eventQueue() {
        return QueueBuilder.durable(EVENT_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", EVENT_ACTIVATION_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue accountQueue() {
        return QueueBuilder.durable(ACCOUNT_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", ACCOUNT_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding eventBinding(TopicExchange eventExchange, Queue eventQueue) {
        return BindingBuilder.bind(eventQueue).to(eventExchange).with(EVENT_ACTIVATION_ROUTING_KEY);
    }

    @Bean
    public Binding accountBinding(TopicExchange eventExchange, Queue accountQueue) {
        return BindingBuilder.bind(accountQueue).to(eventExchange).with(ACCOUNT_ROUTING_KEY);
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange(DLX_EXCHANGE);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ).build();
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with("#");
    }
}
