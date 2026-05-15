package com.focela.platform.websocket.core.sender.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.kafka.annotation.KafkaListener;

/**
 * Consumer for {@link KafkaWebSocketMessage} broadcast messages — actually delivers the message.
 */
@RequiredArgsConstructor
public class KafkaWebSocketMessageConsumer {

    private final KafkaWebSocketMessageSender kafkaWebSocketMessageSender;

    @RabbitHandler
    @KafkaListener(
            topics = "${focela.websocket.sender-kafka.topic}",
            // Append a UUID suffix to the group ID so each Consumer joins a different group, achieving broadcast consumption.
            groupId = "${focela.websocket.sender-kafka.consumer-group}" + "-" + "#{T(java.util.UUID).randomUUID()}")
    public void onMessage(KafkaWebSocketMessage message) {
        kafkaWebSocketMessageSender.send(message.getSessionId(),
                message.getUserType(), message.getUserId(),
                message.getMessageType(), message.getMessageContent());
    }

}
