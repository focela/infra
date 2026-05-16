package com.focela.platform.system.config.web;

import com.focela.platform.swagger.config.FocelaSwaggerAutoConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the web components of the system module
 */
@Configuration(proxyBeanMethods = false)
public class SystemWebConfiguration {

    /**
     * API group of the system module
     */
    @Bean
    public GroupedOpenApi systemGroupedOpenApi() {
        return FocelaSwaggerAutoConfiguration.buildGroupedOpenApi("system");
    }

}
