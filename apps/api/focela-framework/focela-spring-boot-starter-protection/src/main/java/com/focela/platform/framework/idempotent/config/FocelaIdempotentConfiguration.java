package com.focela.platform.framework.idempotent.config;

import com.focela.platform.framework.idempotent.core.aop.IdempotentAspect;
import com.focela.platform.framework.idempotent.core.keyresolver.impl.DefaultIdempotentKeyResolver;
import com.focela.platform.framework.idempotent.core.keyresolver.impl.ExpressionIdempotentKeyResolver;
import com.focela.platform.framework.idempotent.core.keyresolver.IdempotentKeyResolver;
import com.focela.platform.framework.idempotent.core.keyresolver.impl.UserIdempotentKeyResolver;
import com.focela.platform.framework.idempotent.core.redis.IdempotentRedisDAO;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import com.focela.platform.framework.redis.config.FocelaRedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

@AutoConfiguration(after = FocelaRedisAutoConfiguration.class)
public class FocelaIdempotentConfiguration {

    @Bean
    public IdempotentAspect idempotentAspect(List<IdempotentKeyResolver> keyResolvers, IdempotentRedisDAO idempotentRedisDAO) {
        return new IdempotentAspect(keyResolvers, idempotentRedisDAO);
    }

    @Bean
    public IdempotentRedisDAO idempotentRedisDAO(StringRedisTemplate stringRedisTemplate) {
        return new IdempotentRedisDAO(stringRedisTemplate);
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
