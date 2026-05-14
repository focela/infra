package com.focela.platform.framework.mq.redis.config;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.focela.platform.framework.common.enums.DocumentEnum;
import com.focela.platform.framework.mq.redis.core.RedisMQTemplate;
import com.focela.platform.framework.mq.redis.core.job.RedisPendingMessageResendJob;
import com.focela.platform.framework.mq.redis.core.job.RedisStreamMessageCleanupJob;
import com.focela.platform.framework.mq.redis.core.pubsub.AbstractRedisChannelMessageListener;
import com.focela.platform.framework.mq.redis.core.stream.AbstractRedisStreamMessageListener;
import com.focela.platform.framework.redis.config.FocelaRedisAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;
import java.util.Properties;

/**
 * Redis message queue consumer configuration class.
 */
@Slf4j
@EnableScheduling // Enable scheduling, used by RedisPendingMessageResendJob for message redelivery
@AutoConfiguration(after = FocelaRedisAutoConfiguration.class)
public class FocelaRedisMQConsumerAutoConfiguration {

    /**
     * Create the container for Redis Pub/Sub broadcast consumption.
     */
    @Bean
    @ConditionalOnBean(AbstractRedisChannelMessageListener.class) // Only register the Redis pubsub listener when AbstractChannelMessageListener is present
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisMQTemplate redisMQTemplate, List<AbstractRedisChannelMessageListener<?>> listeners) {
        // Create the RedisMessageListenerContainer
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        // Set the RedisConnection factory
        container.setConnectionFactory(redisMQTemplate.getRedisTemplate().getRequiredConnectionFactory());
        // Add listeners
        listeners.forEach(listener -> {
            listener.setRedisMQTemplate(redisMQTemplate);
            container.addMessageListener(listener, new ChannelTopic(listener.getChannel()));
            log.info("[redisMessageListenerContainer][register Channel({}) corresponding listener ({})]",
                    listener.getChannel(), listener.getClass().getName());
        });
        return container;
    }

    /**
     * Create the Redis Stream pending-message redelivery job.
     */
    @Bean
    @ConditionalOnBean(AbstractRedisStreamMessageListener.class) // Only register when AbstractStreamMessageListener is present
    public RedisPendingMessageResendJob redisPendingMessageResendJob(List<AbstractRedisStreamMessageListener<?>> listeners,
                                                                     RedisMQTemplate redisTemplate,
                                                                     RedissonClient redissonClient) {
        return new RedisPendingMessageResendJob(listeners, redisTemplate, redissonClient);
    }

    /**
     * Create the Redis Stream message cleanup job.
     */
    @Bean
    @ConditionalOnBean(AbstractRedisStreamMessageListener.class)
    public RedisStreamMessageCleanupJob redisStreamMessageCleanupJob(List<AbstractRedisStreamMessageListener<?>> listeners,
                                                                     RedisMQTemplate redisTemplate,
                                                                     RedissonClient redissonClient) {
        return new RedisStreamMessageCleanupJob(listeners, redisTemplate, redissonClient);
    }

    /**
     * Create the container for Redis Stream cluster consumption.
     *
     * Background: <a href="https://www.geek-book.com/src/docs/redis/redis/redis.io/commands/xreadgroup.html">Redis Stream xreadgroup command</a>
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnBean(AbstractRedisStreamMessageListener.class) // Only register when AbstractStreamMessageListener is present
    public StreamMessageListenerContainer<String, ObjectRecord<String, String>> redisStreamMessageListenerContainer(
            RedisMQTemplate redisMQTemplate, List<AbstractRedisStreamMessageListener<?>> listeners) {
        RedisTemplate<String, ?> redisTemplate = redisMQTemplate.getRedisTemplate();
        checkRedisVersion(redisTemplate);
        // Step 1: create the StreamMessageListenerContainer
        // Build the options
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, String>> containerOptions =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                        .batchSize(10) // Maximum messages pulled per batch
                        .targetType(String.class) // Target type. Uniformly use String and let AbstractStreamMessageListener deserialize
                        .build();
        // Create the container
        StreamMessageListenerContainer<String, ObjectRecord<String, String>> container =
                StreamMessageListenerContainer.create(redisMQTemplate.getRedisTemplate().getRequiredConnectionFactory(), containerOptions);

        // Step 2: register listeners to consume the corresponding Stream topics
        String consumerName = buildConsumerName();
        listeners.parallelStream().forEach(listener -> {
            log.info("[redisStreamMessageListenerContainer][start register StreamKey({}) corresponding listener ({})]",
                    listener.getStreamKey(), listener.getClass().getName());
            // Create the consumer group for the listener
            try {
                redisTemplate.opsForStream().createGroup(listener.getStreamKey(), listener.getGroup());
            } catch (Exception ignore) {
            }
            // Set the redisTemplate for the listener
            listener.setRedisMQTemplate(redisMQTemplate);
            // Create the Consumer
            Consumer consumer = Consumer.from(listener.getGroup(), consumerName);
            // Set the consumer's offset, based on the smallest consumption offset
            StreamOffset<String> streamOffset = StreamOffset.create(listener.getStreamKey(), ReadOffset.lastConsumed());
            // Configure the consumer subscription
            StreamMessageListenerContainer.StreamReadRequestBuilder<String> builder = StreamMessageListenerContainer.StreamReadRequest
                    .builder(streamOffset).consumer(consumer)
                    .autoAcknowledge(false) // Do not auto-ack
                    .cancelOnError(throwable -> false); // The default cancels consumption on error, which is not what we want; set to false
            container.register(builder.build(), listener);
            log.info("[redisStreamMessageListenerContainer][complete register StreamKey({}) corresponding listener ({})]",
                    listener.getStreamKey(), listener.getClass().getName());
        });
        return container;
    }

    /**
     * Build the consumer name using the local IP plus process ID.
     * Modeled after RocketMQ's clientId implementation.
     *
     * @return consumer name
     */
    public static String buildConsumerName() {
        return String.format("%s@%d", SystemUtil.getHostInfo().getAddress(), SystemUtil.getCurrentPID());
    }

    /**
     * Verify that the Redis version meets the minimum requirement.
     */
    public static void checkRedisVersion(RedisTemplate<String, ?> redisTemplate) {
        // Get the Redis version
        Properties info = redisTemplate.execute((RedisCallback<Properties>) RedisServerCommands::info);
        String version = MapUtil.getStr(info, "redis_version");
        // Require version >= 5.0.0
        int majorVersion = Integer.parseInt(StrUtil.subBefore(version, '.', false));
        if (majorVersion < 5) {
            throw new IllegalStateException(StrUtil.format("Your current Redis version is {}, which is below the minimum required 5.0.0. " +
                    "Please follow the {} documentation to install a newer version.", version, DocumentEnum.REDIS_INSTALL.getUrl()));
        }
    }

}
