package com.rabbit.subscribe.configuration.rabbit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQSender {
    private static final String RABBIT_EXCHANGE = "rabbit-exchange";
    private static final String RABBIT_ROUTING = "rabbit.subscription";

    private static final Logger log = LoggerFactory.getLogger(RabbitMQSender.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public RabbitMQSender(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendMessage(Object data) throws JsonProcessingException {
        String msg = objectMapper.writeValueAsString(data);
        log.info("Sending Notification={}", msg);
        rabbitTemplate.setExchange(RABBIT_EXCHANGE);
        rabbitTemplate.setRoutingKey(RABBIT_ROUTING);
        rabbitTemplate.convertAndSend(data);
        log.info("Has ben success sent to the queue this Notification={}", msg);
    }

}