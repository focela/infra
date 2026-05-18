package com.focela.platform.signature.config;

import com.focela.platform.redis.config.FocelaRedisAutoConfiguration;
import com.focela.platform.signature.core.aop.ApiSignatureAspect;
import com.focela.platform.signature.core.redis.ApiSignatureRedisRepository;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Auto-configuration for HTTP API signatures.
 */
@AutoConfiguration(after = FocelaRedisAutoConfiguration.class)
public class FocelaApiSignatureAutoConfiguration {

    @Bean
    public ApiSignatureAspect signatureAspect(ApiSignatureRedisRepository signatureRedisRepository) {
        return new ApiSignatureAspect(signatureRedisRepository);
    }

    @Bean
    public ApiSignatureRedisRepository signatureRedisRepository(StringRedisTemplate stringRedisTemplate) {
        return new ApiSignatureRedisRepository(stringRedisTemplate);
    }

}
