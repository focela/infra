package com.focela.platform.system.config.sms;

import com.focela.platform.system.config.sms.client.SmsClientFactory;
import com.focela.platform.system.config.sms.client.impl.DefaultSmsClientFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SMS configuration class, including SMS client and SMS verification code
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SmsCodeProperties.class)
public class SmsConfiguration {

    @Bean
    public SmsClientFactory smsClientFactory() {
        return new DefaultSmsClientFactory();
    }

}
