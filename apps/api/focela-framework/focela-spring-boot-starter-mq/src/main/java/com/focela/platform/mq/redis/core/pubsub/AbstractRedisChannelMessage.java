package com.focela.platform.mq.redis.core.pubsub;

import com.focela.platform.mq.redis.core.message.AbstractRedisMessage;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Abstract Redis channel message.
 */
public abstract class AbstractRedisChannelMessage extends AbstractRedisMessage {

    /**
     * Get the Redis channel; defaults to the class name.
     *
     * @return channel
     */
    @JsonIgnore // Avoid serialization, because Redis already specifies the channel when publishing the message.
    public String getChannel() {
        return getClass().getSimpleName();
    }

}
