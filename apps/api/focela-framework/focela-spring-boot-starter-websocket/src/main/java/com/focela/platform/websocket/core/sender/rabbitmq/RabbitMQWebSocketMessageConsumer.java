package com.focela.platform.websocket.core.sender.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;

/**
 * Consumer for {@link RabbitMQWebSocketMessage} broadcast messages; it actually sends the message out.
 */
@RabbitListener(
        bindings = @QueueBinding(
                value = @Queue(
                        // Append a UUID suffix to the Queue name. This way, each launched Consumer has a different Queue, achieving broadcast consumption.
                        name = "${focela.websocket.sender-rabbitmq.queue}" + "-" + "#{T(java.util.UUID).randomUUID()}",
                        // When the Consumer shuts down, the queue can be automatically deleted.
                        autoDelete = "true"
                ),
                exchange = @Exchange(
                        name = "${focela.websocket.sender-rabbitmq.exchange}",
                        type = ExchangeTypes.TOPIC,
                        declare = "false"
                )
        )
)
@RequiredArgsConstructor
public class RabbitMQWebSocketMessageConsumer {

    private final RabbitMQWebSocketMessageSender rabbitMQWebSocketMessageSender;

    @RabbitHandler
    public void onMessage(RabbitMQWebSocketMessage message) {
        rabbitMQWebSocketMessageSender.send(message.getSessionId(),
                message.getUserType(), message.getUserId(),
                message.getMessageType(), message.getMessageContent());
    }

}
