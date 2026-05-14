package com.focela.platform.framework.mq.redis.core.interceptor;

import com.focela.platform.framework.mq.redis.core.message.AbstractRedisMessage;

/**
 * Message interceptor for {@link AbstractRedisMessage}.
 * Acts as a plugin mechanism for extensions, for example MQ message handling in
 * multi-tenant scenarios.
 */
public interface RedisMessageInterceptor {

    default void sendMessageBefore(AbstractRedisMessage message) {
    }

    default void sendMessageAfter(AbstractRedisMessage message) {
    }

    default void consumeMessageBefore(AbstractRedisMessage message) {
    }

    default void consumeMessageAfter(AbstractRedisMessage message) {
    }

}
