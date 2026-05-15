package com.focela.platform.mq.redis.core.stream;

import com.focela.platform.mq.redis.core.message.AbstractRedisMessage;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Abstract Redis Stream Message.
 */
public abstract class AbstractRedisStreamMessage extends AbstractRedisMessage {

    /**
     * Get the Redis Stream key; defaults to the class simple name.
     *
     * @return Channel
     */
    @JsonIgnore // Skip serialization
    public String getStreamKey() {
        return getClass().getSimpleName();
    }

}
