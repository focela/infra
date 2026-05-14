package com.focela.platform.framework.apilog.config;

import com.focela.platform.framework.apilog.core.filter.ApiAccessLogFilter;
import com.focela.platform.framework.apilog.core.interceptor.ApiAccessLogInterceptor;
import com.focela.platform.framework.common.contract.infra.logger.ApiAccessLogContractApi;
import com.focela.platform.framework.common.enums.WebFilterOrderEnum;
import com.focela.platform.framework.web.config.WebProperties;
import com.focela.platform.framework.web.config.FocelaWebAutoConfiguration;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AutoConfiguration(after = FocelaWebAutoConfiguration.class)
public class FocelaApiLogAutoConfiguration implements WebMvcConfigurer {

    /**
     * Create ApiAccessLogFilter Bean to log API requests
     */
    @Bean
    @ConditionalOnProperty(prefix = "focela.access-log", value = "enable", matchIfMissing = true)
    public FilterRegistrationBean<ApiAccessLogFilter> apiAccessLogFilter(WebProperties webProperties,
                                                                         @Value("${spring.application.name}") String applicationName,
                                                                         ApiAccessLogContractApi apiAccessLogApi) {
        ApiAccessLogFilter filter = new ApiAccessLogFilter(webProperties, applicationName, apiAccessLogApi);
        return createFilterBean(filter, WebFilterOrderEnum.API_ACCESS_LOG_FILTER);
    }

    private static <T extends Filter> FilterRegistrationBean<T> createFilterBean(T filter, Integer order) {
        FilterRegistrationBean<T> bean = new FilterRegistrationBean<>(filter);
        bean.setOrder(order);
        return bean;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ApiAccessLogInterceptor());
    }

}
