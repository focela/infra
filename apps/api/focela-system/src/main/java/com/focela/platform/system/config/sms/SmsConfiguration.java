package com.focela.platform.system.config.sms;

import com.focela.platform.system.config.sms.core.client.SmsClientFactory;
import com.focela.platform.system.config.sms.core.client.impl.DefaultSmsClientFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 短信配置类，包括短信客户端、短信验证码两部分
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SmsCodeProperties.class)
public class SmsConfiguration {

    @Bean
    public SmsClientFactory smsClientFactory() {
        return new DefaultSmsClientFactory();
    }

}
