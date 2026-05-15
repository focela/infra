package com.focela.platform.infra.config.file;

import com.focela.platform.infra.config.file.core.client.FileClientFactory;
import com.focela.platform.infra.config.file.core.client.DefaultFileClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * File config class
 */
@Configuration(proxyBeanMethods = false)
public class FocelaFileAutoConfiguration {

    @Bean
    public FileClientFactory fileClientFactory() {
        return new DefaultFileClientFactory();
    }

}
