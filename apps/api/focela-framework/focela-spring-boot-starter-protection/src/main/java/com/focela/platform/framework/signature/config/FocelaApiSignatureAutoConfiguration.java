package com.focela.platform.framework.signature.config;

import com.focela.platform.framework.redis.config.FocelaRedisAutoConfiguration;
import com.focela.platform.framework.signature.core.aop.ApiSignatureAspect;
import com.focela.platform.framework.signature.core.redis.ApiSignatureRedisDAO;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * HTTP API 签名的自动配置类
 */
@AutoConfiguration(after = FocelaRedisAutoConfiguration.class)
public class FocelaApiSignatureAutoConfiguration {

    @Bean
    public ApiSignatureAspect signatureAspect(ApiSignatureRedisDAO signatureRedisDAO) {
        return new ApiSignatureAspect(signatureRedisDAO);
    }

    @Bean
    public ApiSignatureRedisDAO signatureRedisDAO(StringRedisTemplate stringRedisTemplate) {
        return new ApiSignatureRedisDAO(stringRedisTemplate);
    }

}
