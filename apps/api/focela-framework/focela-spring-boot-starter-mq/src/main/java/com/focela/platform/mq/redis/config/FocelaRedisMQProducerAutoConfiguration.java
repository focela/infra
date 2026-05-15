package com.focela.platform.mq.redis.config;

import com.focela.platform.mq.redis.core.RedisMQTemplate;
import com.focela.platform.mq.redis.core.interceptor.RedisMessageInterceptor;
import com.focela.platform.redis.config.FocelaRedisAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

/**
 * Redis message queue producer configuration class.
 */
@Slf4j
@AutoConfiguration(after = FocelaRedisAutoConfiguration.class)
public class FocelaRedisMQProducerAutoConfiguration {

    @Bean
    public RedisMQTemplate redisMQTemplate(StringRedisTemplate redisTemplate,
                                           List<RedisMessageInterceptor> interceptors) {
        RedisMQTemplate redisMQTemplate = new RedisMQTemplate(redisTemplate);
        // Add interceptors
        interceptors.forEach(redisMQTemplate::addInterceptor);
        return redisMQTemplate;
    }

}
