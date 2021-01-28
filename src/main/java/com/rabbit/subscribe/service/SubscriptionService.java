package com.rabbit.subscribe.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rabbit.subscribe.domain.Subscription;
import com.rabbit.subscribe.domain.SubscriptionHistory;
import com.rabbit.subscribe.domain.dto.SubscriptionDTO;
import com.rabbit.subscribe.domain.enumerated.SubscriptionType;
import com.rabbit.subscribe.configuration.rabbit.RabbitMQSender;
import com.rabbit.subscribe.repository.SubscriptionHistoryRepository;
import com.rabbit.subscribe.repository.SubscriptionRepository;
import io.micrometer.core.instrument.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static com.rabbit.subscribe.domain.enumerated.SubscriptionType.SUBSCRIPTION_CANCELED;

@Service
@PropertySource(value = {"classpath:rabbitMQ.yml"})
public class SubscriptionService {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionService.class);

    private final RabbitMQSender rabbitMQSender;
    private final SubscriptionRepository repository;
    private final SubscriptionHistoryRepository historyRepository;

    @Value("${exchange}")
    String exchangeName;
    @Value("${routing}")
    String purchasedOrigin;
    @Value("${subscription}")
    String purchasedSubscription;

    public SubscriptionService(RabbitMQSender rabbitMQSender, SubscriptionRepository repository, SubscriptionHistoryRepository historyRepository) {
        this.rabbitMQSender = rabbitMQSender;
        this.repository = repository;
        this.historyRepository = historyRepository;
    }

    public SubscriptionDTO assign(String subscription) throws JsonProcessingException {
        if (StringUtils.isNotBlank(subscription)) {
            rabbitMQSender.sendMessage(
                    SubscriptionDTO.builder()
                            .subscription(StringUtils.isNotBlank(subscription) ? subscription : null)
                            .notificationType(SubscriptionType.SUBSCRIPTION_PURCHASED.getCode())
                            .build()
            );
            return SubscriptionDTO.builder()
                    .subscription(subscription)
                    .notificationType(SubscriptionType.SUBSCRIPTION_PURCHASED.getCode())
                    .build();
        }
        return SubscriptionDTO.builder().subscription("User not found!").build();
    }

    public String cancel(String subscription) throws JsonProcessingException {
        if (StringUtils.isNotBlank(subscription)) {
            if (!repository.findById(subscription).isPresent()) {
                return "Subscription not found";
            }
            rabbitMQSender.sendMessage(
                    SubscriptionDTO.builder()
                            .subscription(subscription)
                            .notificationType(SUBSCRIPTION_CANCELED.getCode())
                            .build()
            );
            return SUBSCRIPTION_CANCELED.getType();
        }
        return "";
    }

    public String renovation(String subscription) throws JsonProcessingException {
        if (StringUtils.isNotBlank(subscription)) {
            if (!repository.findById(subscription).isPresent()) {
                return "Subscription not found";
            }
            rabbitMQSender.sendMessage(
                    SubscriptionDTO.builder()
                            .subscription(subscription)
                            .notificationType(SubscriptionType.SUBSCRIPTION_RESTARTED.getCode())
                            .build()
            );
            return SubscriptionType.SUBSCRIPTION_RESTARTED.getType();
        }

        return "";
    }

    public SubscriptionDTO findStatus(String subscription) {
        Subscription s = this.get(subscription);
        return SubscriptionDTO
                .builder()
                .notificationType(s.getStatus().getType())
                .subscription(s.getId())
                .build();
    }

    public void receivedAndSendTransaction(SubscriptionDTO subscriptionDTO) {
        log.info("Received new Subscription={} with status : Notification={}", subscriptionDTO.getSubscription(), subscriptionDTO.getNotificationType());
        SubscriptionType type = SubscriptionType.selectStatusReceived(subscriptionDTO.getNotificationType());
        Subscription old = this.get(subscriptionDTO.getSubscription());

        if (old.getCreatedAt() != null) {
            post(old
                    .toBuilder()
                    .updatedAt(Timestamp.valueOf(LocalDateTime.now()))
                    .status(type)
                    .build());
        } else {
            post(Subscription
                    .builder()
                    .status(SubscriptionType.SUBSCRIPTION_PURCHASED)
                    .id(subscriptionDTO.getSubscription())
                    .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                    .build());
        }
    }

    private Subscription get(String id) {
        return repository.findById(id)
                .orElse(Subscription
                        .builder()
                        .build());
    }

    private void post(Subscription subscription) {
        log.info("Post a new notification : Status={} for this Subscription={}", subscription.getStatus().getType(), subscription.getId());
        repository.saveAndFlush(subscription);
        historyRepository.save(SubscriptionHistory.builder()
                .subscription(subscription)
                .type(subscription.getStatus())
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .build());
    }
}
