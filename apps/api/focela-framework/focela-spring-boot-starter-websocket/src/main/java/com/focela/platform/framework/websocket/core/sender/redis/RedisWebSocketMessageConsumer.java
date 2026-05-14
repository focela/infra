package com.focela.platform.framework.websocket.core.sender.redis;

import com.focela.platform.framework.mq.redis.core.pubsub.AbstractRedisChannelMessageListener;
import lombok.RequiredArgsConstructor;

/**
 * Consumer for {@link RedisWebSocketMessage} broadcast messages; it actually sends the message out.
 */
@RequiredArgsConstructor
public class RedisWebSocketMessageConsumer extends AbstractRedisChannelMessageListener<RedisWebSocketMessage> {

    private final RedisWebSocketMessageSender redisWebSocketMessageSender;

    @Override
    public void onMessage(RedisWebSocketMessage message) {
        redisWebSocketMessageSender.send(message.getSessionId(),
                message.getUserType(), message.getUserId(),
                message.getMessageType(), message.getMessageContent());
    }

}
