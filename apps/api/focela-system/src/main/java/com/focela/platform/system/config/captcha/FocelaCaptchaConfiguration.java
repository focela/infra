package com.focela.platform.system.config.captcha;

import com.focela.platform.system.config.captcha.DefaultRedisCaptchaService;
import com.anji.captcha.config.AjCaptchaAutoConfiguration;
import com.anji.captcha.properties.AjCaptchaProperties;
import com.anji.captcha.service.CaptchaCacheService;
import com.anji.captcha.service.impl.CaptchaServiceFactory;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Captcha configuration class
 */
@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration(AjCaptchaAutoConfiguration.class) // Purpose: work around aj-captcha auto-configuration not taking effect on SpringBoot 3.x
public class FocelaCaptchaConfiguration {

    @Bean(name = "AjCaptchaCacheService")
    @Primary
    public CaptchaCacheService captchaCacheService(AjCaptchaProperties config,
                                                   StringRedisTemplate stringRedisTemplate) {
        CaptchaCacheService captchaCacheService = CaptchaServiceFactory.getCache(config.getCacheType().name());
        if (captchaCacheService instanceof DefaultRedisCaptchaService) {
            ((DefaultRedisCaptchaService) captchaCacheService).setStringRedisTemplate(stringRedisTemplate);
        }
        return captchaCacheService;
    }

}
