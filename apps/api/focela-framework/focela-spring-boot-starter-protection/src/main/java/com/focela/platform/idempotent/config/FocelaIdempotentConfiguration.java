package com.focela.platform.idempotent.config;

import com.focela.platform.idempotent.core.aop.IdempotentAspect;
import com.focela.platform.idempotent.core.keyresolver.impl.DefaultIdempotentKeyResolver;
import com.focela.platform.idempotent.core.keyresolver.impl.ExpressionIdempotentKeyResolver;
import com.focela.platform.idempotent.core.keyresolver.IdempotentKeyResolver;
import com.focela.platform.idempotent.core.keyresolver.impl.UserIdempotentKeyResolver;
import com.focela.platform.idempotent.core.redis.IdempotentRedisRepository;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import com.focela.platform.redis.config.FocelaRedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

@AutoConfiguration(after = FocelaRedisAutoConfiguration.class)
public class FocelaIdempotentConfiguration {

    @Bean
    public IdempotentAspect idempotentAspect(List<IdempotentKeyResolver> keyResolvers, IdempotentRedisRepository idempotentRedisRepository) {
        return new IdempotentAspect(keyResolvers, idempotentRedisRepository);
    }

    @Bean
    public IdempotentRedisRepository idempotentRedisRepository(StringRedisTemplate stringRedisTemplate) {
        return new IdempotentRedisRepository(stringRedisTemplate);
    }

    // ========== IdempotentKeyResolver Beans ==========

    @Bean
    public DefaultIdempotentKeyResolver defaultIdempotentKeyResolver() {
        return new DefaultIdempotentKeyResolver();
    }

    @Bean
    public UserIdempotentKeyResolver userIdempotentKeyResolver() {
        return new UserIdempotentKeyResolver();
    }

    @Bean
    public ExpressionIdempotentKeyResolver expressionIdempotentKeyResolver() {
        return new ExpressionIdempotentKeyResolver();
    }

}
