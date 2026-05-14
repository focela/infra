package com.focela.platform.framework.mq.redis.core;

import com.focela.platform.framework.common.utils.json.JsonUtils;
import com.focela.platform.framework.mq.redis.core.interceptor.RedisMessageInterceptor;
import com.focela.platform.framework.mq.redis.core.message.AbstractRedisMessage;
import com.focela.platform.framework.mq.redis.core.pubsub.AbstractRedisChannelMessage;
import com.focela.platform.framework.mq.redis.core.stream.AbstractRedisStreamMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Redis MQ operation template class.
 */
@AllArgsConstructor
public class RedisMQTemplate {

    @Getter
    private final RedisTemplate<String, ?> redisTemplate;
    /**
     * Interceptor list.
     */
    @Getter
    private final List<RedisMessageInterceptor> interceptors = new ArrayList<>();

    /**
     * Send a Redis message via Redis pub/sub.
     *
     * @param message message
     */
    public <T extends AbstractRedisChannelMessage> void send(T message) {
        try {
            sendMessageBefore(message);
            // Send the message
            redisTemplate.convertAndSend(message.getChannel(), JsonUtils.toJsonString(message));
        } finally {
            sendMessageAfter(message);
        }
    }

    /**
     * Send a Redis message via Redis Stream.
     *
     * @param message message
     * @return message record ID
     */
    public <T extends AbstractRedisStreamMessage> RecordId send(T message) {
        try {
            sendMessageBefore(message);
            // Send the message
            return redisTemplate.opsForStream().add(StreamRecords.newRecord()
                    .ofObject(JsonUtils.toJsonString(message)) // Set the payload
                    .withStreamKey(message.getStreamKey())); // Set the stream key
        } finally {
            sendMessageAfter(message);
        }
    }

    /**
     * Add an interceptor.
     *
     * @param interceptor interceptor
     */
    public void addInterceptor(RedisMessageInterceptor interceptor) {
        interceptors.add(interceptor);
    }

    private void sendMessageBefore(AbstractRedisMessage message) {
        // Forward order
        interceptors.forEach(interceptor -> interceptor.sendMessageBefore(message));
    }

    private void sendMessageAfter(AbstractRedisMessage message) {
        // Reverse order
        for (int i = interceptors.size() - 1; i >= 0; i--) {
            interceptors.get(i).sendMessageAfter(message);
        }
    }

}
