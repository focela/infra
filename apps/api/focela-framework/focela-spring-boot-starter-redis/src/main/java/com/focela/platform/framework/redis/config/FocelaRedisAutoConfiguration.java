package com.focela.platform.framework.redis.config;

import cn.hutool.core.util.ReflectUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.redisson.spring.starter.RedissonAutoConfigurationV2;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Redis configuration class.
 */
@AutoConfiguration(before = RedissonAutoConfigurationV2.class) // So our own RedisTemplate Bean is used
public class FocelaRedisAutoConfiguration {

    /**
     * Create the RedisTemplate Bean using JSON serialization.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        // Build the RedisTemplate
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // Wire in the RedisConnection factory - this is what enables the various Java Redis clients under the hood.
        template.setConnectionFactory(factory);
        // Use String serialization for KEY.
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());
        // Use JSON (Jackson) serialization for VALUE.
        template.setValueSerializer(buildRedisSerializer());
        template.setHashValueSerializer(buildRedisSerializer());
        return template;
    }

    public static RedisSerializer<?> buildRedisSerializer() {
        RedisSerializer<Object> json = RedisSerializer.json();
        // Handle LocalDateTime serialization
        ObjectMapper objectMapper = (ObjectMapper) ReflectUtil.getFieldValue(json, "mapper");
        objectMapper.registerModules(new JavaTimeModule());
        return json;
    }

}
