package com.enotessa;

import dto.AuthEvent;
import enums.EventAuthType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AuthEventProducer {
    private static final Logger logger = LoggerFactory.getLogger(AuthEventProducer.class);
    private static final String TOPIC = "user-events";
    
    private final KafkaTemplate<String, AuthEvent> kafkaTemplate;

    public void sendUserRegistered(String userId) {
        String eventName = EventAuthType.REGISTERED_EVENT.getDisplayName();
        AuthEvent event = new AuthEvent(eventName, userId, LocalDateTime.now());
        sendEvent(event);
    }

    public void sendUserLoggedIn(String userId) {
        String eventName = EventAuthType.LOGGED_IN_EVENT.getDisplayName();
        AuthEvent event = new AuthEvent(eventName, userId, LocalDateTime.now());
        sendEvent(event);
    }

    public void sendUserLoggedOut(String userId) {
        String eventName = EventAuthType.LOGGED_OUT_EVENT.getDisplayName();
        AuthEvent event = new AuthEvent(eventName, userId, LocalDateTime.now());
        sendEvent(event);
    }

    private void sendEvent(AuthEvent event) {
        kafkaTemplate.send(TOPIC, event.getUserId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        logger.error("Ошибка отправки события {} в Kafka: {}", event, ex.getMessage(), ex);
                    } else {
                        logger.info("Событие {} отправлено в Kafka, topic={}, offset={}",
                                event, result.getRecordMetadata().topic(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
