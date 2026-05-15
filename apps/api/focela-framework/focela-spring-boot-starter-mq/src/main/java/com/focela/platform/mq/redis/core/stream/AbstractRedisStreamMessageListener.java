package com.focela.platform.mq.redis.core.stream;

import cn.hutool.core.util.TypeUtil;
import com.focela.platform.common.utils.json.JsonUtils;
import com.focela.platform.mq.redis.core.RedisMQTemplate;
import com.focela.platform.mq.redis.core.interceptor.RedisMessageInterceptor;
import com.focela.platform.mq.redis.core.message.AbstractRedisMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamListener;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Abstract Redis Stream listener used to implement cluster consumption.
 *
 * @param <T> message type. Must be specified; otherwise an error is thrown.
 */
public abstract class AbstractRedisStreamMessageListener<T extends AbstractRedisStreamMessage>
        implements StreamListener<String, ObjectRecord<String, String>> {

    /**
     * Message type.
     */
    private final Class<T> messageType;
    /**
     * Redis Channel.
     */
    @Getter
    private final String streamKey;

    /**
     * Redis consumer group; defaults to spring.application.name.
     */
    @Value("${spring.application.name}")
    @Getter
    private String group;
    /**
     * RedisMQTemplate.
     */
    @Setter
    private RedisMQTemplate redisMQTemplate;

    @SneakyThrows
    protected AbstractRedisStreamMessageListener() {
        this.messageType = getMessageClass();
        this.streamKey = messageType.getDeclaredConstructor().newInstance().getStreamKey();
    }

    protected AbstractRedisStreamMessageListener(String streamKey, String group) {
        this.messageType = null;
        this.streamKey = streamKey;
        this.group = group;
    }

    @Override
    public void onMessage(ObjectRecord<String, String> message) {
        // Consume the message
        T messageObj = JsonUtils.parseObject(message.getValue(), messageType);
        try {
            consumeMessageBefore(messageObj);
            // Consume the message
            this.onMessage(messageObj);
            // Acknowledge message consumption
            redisMQTemplate.getRedisTemplate().opsForStream().acknowledge(group, message);
            // TODO: still to consider:
            // 1. exception handling
            // 2. send logging; combined with transactions
            // 3. consume logging; and generic idempotency
            // 4. retry on consume failure, https://zhuanlan.zhihu.com/p/60501638
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
