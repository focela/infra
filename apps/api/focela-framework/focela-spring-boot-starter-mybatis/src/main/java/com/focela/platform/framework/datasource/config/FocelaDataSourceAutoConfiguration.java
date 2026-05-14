package com.focela.platform.framework.datasource.config;

import com.focela.platform.framework.datasource.core.filter.DruidAdRemoveFilter;
import com.alibaba.druid.spring.boot3.autoconfigure.properties.DruidStatProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Database configuration class
 */
@AutoConfiguration
@EnableTransactionManagement(proxyTargetClass = true) // Enable transaction management
@EnableConfigurationProperties(DruidStatProperties.class)
public class FocelaDataSourceAutoConfiguration {

    /**
     * Create the DruidAdRemoveFilter to filter out common.js ads
     */
    @Bean
    @ConditionalOnProperty(name = "spring.datasource.druid.stat-view-servlet.enabled", havingValue = "true")
    public FilterRegistrationBean<DruidAdRemoveFilter> druidAdRemoveFilterFilter(DruidStatProperties properties) {
        // Get the druid web monitoring page parameters
        DruidStatProperties.StatViewServlet config = properties.getStatViewServlet();
        // Extract the configuration path for common.js
        String pattern = config.getUrlPattern() != null ? config.getUrlPattern() : "/druid/*";
        String commonJsPattern = pattern.replaceAll("\\*", "js/common.js");
        // Create the DruidAdRemoveFilter Bean
        FilterRegistrationBean<DruidAdRemoveFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new DruidAdRemoveFilter());
        registrationBean.addUrlPatterns(commonJsPattern);
        return registrationBean;
    }

}
