package com.focela.platform.websocket.core.sender.rocketmq;

import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;

/**
 * Consumer for {@link RocketMQWebSocketMessage} broadcast messages — actually delivers the message.
 */
@RocketMQMessageListener( // Important: add @RocketMQMessageListener to declare the consumed topic
        topic = "${focela.websocket.sender-rocketmq.topic}",
        consumerGroup = "${focela.websocket.sender-rocketmq.consumer-group}",
        messageModel = MessageModel.BROADCASTING // Broadcast mode, so every instance receives the message
)
@RequiredArgsConstructor
public class RocketMQWebSocketMessageConsumer implements RocketMQListener<RocketMQWebSocketMessage> {

    private final RocketMQWebSocketMessageSender rocketMQWebSocketMessageSender;

    @Override
    public void onMessage(RocketMQWebSocketMessage message) {
        rocketMQWebSocketMessageSender.send(message.getSessionId(),
                message.getUserType(), message.getUserId(),
                message.getMessageType(), message.getMessageContent());
    }

}
