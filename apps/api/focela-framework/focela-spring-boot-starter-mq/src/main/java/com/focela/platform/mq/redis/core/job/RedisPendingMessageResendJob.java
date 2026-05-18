package com.focela.platform.mq.redis.core.job;

import cn.hutool.core.collection.CollUtil;
import com.focela.platform.mq.redis.core.RedisMQTemplate;
import com.focela.platform.mq.redis.core.stream.AbstractRedisStreamMessageListener;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.PendingMessages;
import org.springframework.data.redis.connection.stream.PendingMessagesSummary;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Job that handles messages left unconsumed after a consumer crash.
 */
@Slf4j
@AllArgsConstructor
public class RedisPendingMessageResendJob {

    private static final String LOCK_KEY = "redis:stream:pending-message-resend:lock";

    /**
     * Message timeout; default 5 minutes.
     *
     * 1. Only timed-out messages get redelivered.
     * 2. The scheduled task runs once per minute, so messages are not redelivered
     *    immediately after timing out; in the worst case it takes up to 1 extra
     *    minute after the 5-minute expiration before the message is picked up.
     */
    private static final int EXPIRE_TIME = 5 * 60;

    private final List<AbstractRedisStreamMessageListener<?>> listeners;
    private final RedisMQTemplate redisTemplate;
    private final RedissonClient redissonClient;

    /**
     * Runs once a minute at the 35-second mark to avoid contention with tasks that fire on the minute.
     */
    @Scheduled(cron = "35 * * * * ?")
    public void messageResend() {
        RLock lock = redissonClient.getLock(LOCK_KEY);
        // Try to acquire the lock
        if (lock.tryLock()) {
            try {
                execute();
            } catch (Exception ex) {
                log.error("[messageResend][execute exception]", ex);
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * Execute the cleanup logic.
     *
     * @see <a href="https://gitee.com/zhijiantianya/ruoyi-vue-pro/pulls/480/files">discussion</a>
     */
    private void execute() {
        StreamOperations<String, Object, Object> ops = redisTemplate.getRedisTemplate().opsForStream();
        listeners.forEach(listener -> {
            PendingMessagesSummary pendingMessagesSummary = Objects.requireNonNull(ops.pending(listener.getStreamKey(), listener.getGroup()));
            // Pending message count per consumer
            Map<String, Long> pendingMessagesPerConsumer = pendingMessagesSummary.getPendingMessagesPerConsumer();
            pendingMessagesPerConsumer.forEach((consumerName, pendingMessageCount) -> {
                log.info("[processPendingMessage][consumer({}) message count ({})]", consumerName, pendingMessageCount);
                // Pending message details for each consumer
                PendingMessages pendingMessages = ops.pending(listener.getStreamKey(), Consumer.from(listener.getGroup(), consumerName), Range.unbounded(), pendingMessageCount);
                if (pendingMessages.isEmpty()) {
                    return;
                }
                pendingMessages.forEach(pendingMessage -> {
                    // Time elapsed since the message was last delivered to the consumer
                    long lastDelivery = pendingMessage.getElapsedTimeSinceLastDelivery().getSeconds();
                    if (lastDelivery < EXPIRE_TIME){
                        return;
                    }
                    // Fetch the message body by ID
                    List<MapRecord<String, Object, Object>> records = ops.range(listener.getStreamKey(),
                            Range.of(Range.Bound.inclusive(pendingMessage.getIdAsString()), Range.Bound.inclusive(pendingMessage.getIdAsString())));
                    if (CollUtil.isEmpty(records)) {
                        return;
                    }
                    // Redeliver the message
                    redisTemplate.getRedisTemplate().opsForStream().add(StreamRecords.newRecord()
                            .ofObject(records.get(0).getValue()) // Set the payload
                            .withStreamKey(listener.getStreamKey()));
                    // Acknowledge consumption
                    redisTemplate.getRedisTemplate().opsForStream().acknowledge(listener.getGroup(), records.get(0));
                    log.info("[processPendingMessage][message ({}) redelivered successfully]", records.get(0).getId());
                });
            });
        });
    }
}
