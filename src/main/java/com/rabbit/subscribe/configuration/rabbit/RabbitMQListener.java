package com.rabbit.subscribe.configuration.rabbit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbit.subscribe.domain.dto.SubscriptionDTO;
import com.rabbit.subscribe.service.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = {"classpath:rabbitMQ.yml"})
public class RabbitMQListener implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQListener.class);
    private static final String RABBIT_QUEUE = "rabbit-queue";
    private static final String RABBIT_ROUTING = "rabbit.subscription";
    private static final String RABBIT_EXCHANGE = "rabbit-exchange";

    private final RabbitMQ rabbitMQ;
    private final SubscriptionService service;
    private final ObjectMapper objectMapper;


    public RabbitMQListener(RabbitMQ rabbitMQ, SubscriptionService service, ObjectMapper objectMapper) {
        this.rabbitMQ = rabbitMQ;
        this.service = service;
        this.objectMapper = objectMapper;
    }

    @Override
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = RABBIT_QUEUE, durable = "true"),
            exchange = @Exchange(name = RABBIT_EXCHANGE, type = ExchangeTypes.TOPIC), key = RABBIT_ROUTING))
    public void onMessage(Message message) {
        log.info("This subscription Notification={} ", message);
        SubscriptionDTO received = null;
        try {
            received = objectMapper.readValue(objectMapper.writeValueAsString(rabbitMQ.converter()
                    .fromMessage(message)), SubscriptionDTO.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        log.info("This subscription received. Received={} ", received);
        assert received != null;
        SubscriptionDTO
                .builder()
                .subscription(received.getSubscription())
                .notificationType(received.getNotificationType())
                .build();
        service.receivedAndSendTransaction(received);

    }

}
