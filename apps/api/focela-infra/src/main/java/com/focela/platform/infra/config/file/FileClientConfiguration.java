package com.focela.platform.infra.config.file;

import com.focela.platform.infra.config.file.client.FileClientFactory;
import com.focela.platform.infra.config.file.client.DefaultFileClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "focelaFileAutoConfiguration", proxyBeanMethods = false)
public class FileClientConfiguration {

    @Bean
    public FileClientFactory fileClientFactory() {
        return new DefaultFileClientFactory();
    }

}
