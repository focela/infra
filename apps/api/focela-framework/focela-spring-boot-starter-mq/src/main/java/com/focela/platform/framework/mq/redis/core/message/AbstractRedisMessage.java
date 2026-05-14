package com.focela.platform.framework.mq.redis.core.message;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract base class for Redis messages.
 */
@Data
public abstract class AbstractRedisMessage {

    /**
     * Headers.
     */
    private Map<String, String> headers = new HashMap<>();

    public String getHeader(String key) {
        return headers.get(key);
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

}
