package com.focela.platform.mq.redis.core.job;

import com.focela.platform.mq.redis.core.RedisMQTemplate;
import com.focela.platform.mq.redis.core.stream.AbstractRedisStreamMessageListener;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * Redis Stream message cleanup job.
 * Periodically removes already-consumed messages to keep memory usage in check.
 *
 * @see <a href="https://www.cnblogs.com/nanxiang/p/16179519.html">Notes on a Redis stream data type not releasing memory</a>
 */
@Slf4j
@AllArgsConstructor
public class RedisStreamMessageCleanupJob {

    private static final String LOCK_KEY = "redis:stream:message-cleanup:lock";

    /**
     * Number of messages to retain; defaults to the most recent 10000.
     */
    private static final long MAX_COUNT = 10000;

    private final List<AbstractRedisStreamMessageListener<?>> listeners;
    private final RedisMQTemplate redisTemplate;
    private final RedissonClient redissonClient;

    /**
     * Run the cleanup task once every hour.
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void cleanup() {
        RLock lock = redissonClient.getLock(LOCK_KEY);
        // Try to acquire the lock
        if (lock.tryLock()) {
            try {
                execute();
            } catch (Exception ex) {
                log.error("[cleanup][execute exception]", ex);
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * Execute the cleanup logic.
     */
    private void execute() {
        StreamOperations<String, Object, Object> ops = redisTemplate.getRedisTemplate().opsForStream();
        listeners.forEach(listener -> {
            try {
                // Use the XTRIM command to keep only the most recent MAX_LEN messages
                Long trimCount = ops.trim(listener.getStreamKey(), MAX_COUNT, true);
                if (trimCount != null && trimCount > 0) {
                    log.info("[execute][Stream({}) clean message count ({})]", listener.getStreamKey(), trimCount);
                }
            } catch (Exception ex) {
                log.error("[execute][Stream({}) clean exception]", listener.getStreamKey(), ex);
            }
        });
    }
}