package com.focela.platform.mq.redis.core.pubsub;

import cn.hutool.core.util.TypeUtil;
import com.focela.platform.common.utils.json.JsonUtils;
import com.focela.platform.mq.redis.core.RedisMQTemplate;
import com.focela.platform.mq.redis.core.interceptor.RedisMessageInterceptor;
import com.focela.platform.mq.redis.core.message.AbstractRedisMessage;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Abstract Redis Pub/Sub listener used to implement broadcast consumption.
 *
 * @param <T> message type. Must be specified; otherwise an error is thrown.
 */
public abstract class AbstractRedisChannelMessageListener<T extends AbstractRedisChannelMessage> implements MessageListener {

    /**
     * Message type.
     */
    private final Class<T> messageType;
    /**
     * Redis channel.
     */
    private final String channel;
    /**
     * RedisMQTemplate.
     */
    @Setter
    private RedisMQTemplate redisMQTemplate;

    @SneakyThrows
    protected AbstractRedisChannelMessageListener() {
        this.messageType = getMessageClass();
        this.channel = messageType.getDeclaredConstructor().newInstance().getChannel();
    }

    /**
     * Get the Redis channel that the subscriber listens to.
     *
     * @return channel
     */
    public final String getChannel() {
        return channel;
    }

    @Override
    public final void onMessage(Message message, byte[] bytes) {
        T messageObj = JsonUtils.parseObject(message.getBody(), messageType);
        try {
            consumeMessageBefore(messageObj);
            // Consume the message
            this.onMessage(messageObj);
        } finally {
            consumeMessageAfter(messageObj);
        }
    }

    /**
     * Handle the message.
     *
     * @param message message
     */
    public abstract void onMessage(T message);

    /**
     * Resolve the message type from the class generic parameter.
     *
     * @return message type
     */
    @SuppressWarnings("unchecked")
    private Class<T> getMessageClass() {
        Type type = TypeUtil.getTypeArgument(getClass(), 0);
        if (type == null) {
            throw new IllegalStateException(String.format("type (%s) requires set message type", getClass().getName()));
        }
        return (Class<T>) type;
    }

    private void consumeMessageBefore(AbstractRedisMessage message) {
        assert redisMQTemplate != null;
        List<RedisMessageInterceptor> interceptors = redisMQTemplate.getInterceptors();
        // Forward order
        interceptors.forEach(interceptor -> interceptor.consumeMessageBefore(message));
    }

    private void consumeMessageAfter(AbstractRedisMessage message) {
        assert redisMQTemplate != null;
        List<RedisMessageInterceptor> interceptors = redisMQTemplate.getInterceptors();
        // Reverse order
        for (int i = interceptors.size() - 1; i >= 0; i--) {
            interceptors.get(i).consumeMessageAfter(message);
        }
    }

}
