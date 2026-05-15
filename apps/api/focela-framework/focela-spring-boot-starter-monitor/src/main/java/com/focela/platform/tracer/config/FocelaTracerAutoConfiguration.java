package com.focela.platform.tracer.config;

import com.focela.platform.common.enums.WebFilterOrderEnum;
import com.focela.platform.tracer.core.filter.TraceFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * Tracer configuration class.
 */
@AutoConfiguration
@ConditionalOnClass(name = {
        "org.apache.skywalking.apm.toolkit.opentracing.SkywalkingTracer", // from apm-toolkit-opentracing.jar
//        "io.opentracing.Tracer", // from opentracing-api.jar
        "jakarta.servlet.Filter"
})
@EnableConfigurationProperties(TracerProperties.class)
@ConditionalOnProperty(prefix = "focela.tracer", value = "enable", matchIfMissing = true)
public class FocelaTracerAutoConfiguration {

    // TODO: SkyWalking is not compatible with the latest opentracing version, and opentracing is no longer maintained. Migrate to opentelemetry in a follow-up.
//    @Bean
//    public BusinessTraceAspect bizTracingAop() {
//        return new BusinessTraceAspect(tracer());
//    }
//
//    @Bean
//    public Tracer tracer() {
//        // Create the SkywalkingTracer
//        SkywalkingTracer tracer = new SkywalkingTracer();
//        // Register it as the GlobalTracer
//        GlobalTracer.registerIfAbsent(tracer);
//        return tracer;
//    }

    /**
     * Create the TraceFilter, which sets traceId on the response header.
     */
    @Bean
    public FilterRegistrationBean<TraceFilter> traceFilter() {
        FilterRegistrationBean<TraceFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TraceFilter());
        registrationBean.setOrder(WebFilterOrderEnum.TRACE_FILTER);
        return registrationBean;
    }

}
