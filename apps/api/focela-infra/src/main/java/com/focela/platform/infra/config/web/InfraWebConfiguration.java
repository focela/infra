package com.focela.platform.infra.config.web;

import com.focela.platform.swagger.config.FocelaSwaggerAutoConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the web components of the infra module
 */
@Configuration(proxyBeanMethods = false)
public class InfraWebConfiguration {

    /**
     * API group for the infra module
     */
    @Bean
    public GroupedOpenApi infraGroupedOpenApi() {
        return FocelaSwaggerAutoConfiguration.buildGroupedOpenApi("infra");
    }

}
