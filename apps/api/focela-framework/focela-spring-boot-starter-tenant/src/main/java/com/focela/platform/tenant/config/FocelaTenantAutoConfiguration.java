package com.focela.platform.tenant.config;

import com.focela.platform.common.api.system.tenant.TenantContractApi;
import com.focela.platform.common.enums.WebFilterOrderEnum;
import com.focela.platform.mybatis.core.utils.MyBatisUtils;
import com.focela.platform.redis.config.FocelaCacheProperties;
import com.focela.platform.security.core.service.SecurityFrameworkService;
import com.focela.platform.tenant.core.aop.TenantIgnore;
import com.focela.platform.tenant.core.aop.TenantIgnoreAspect;
import com.focela.platform.tenant.core.db.TenantDatabaseInterceptor;
import com.focela.platform.tenant.core.job.TenantJobAspect;
import com.focela.platform.tenant.core.mq.kafka.TenantKafkaConsumerInitializer;
import com.focela.platform.tenant.core.mq.rabbitmq.TenantRabbitMQInitializer;
import com.focela.platform.tenant.core.mq.redis.TenantRedisMessageInterceptor;
import com.focela.platform.tenant.core.mq.rocketmq.TenantRocketMQInitializer;
import com.focela.platform.tenant.core.redis.TenantRedisCacheManager;
import com.focela.platform.tenant.core.security.TenantSecurityWebFilter;
import com.focela.platform.tenant.core.service.TenantFrameworkService;
import com.focela.platform.tenant.core.service.DefaultTenantFrameworkService;
import com.focela.platform.tenant.core.web.TenantContextWebFilter;
import com.focela.platform.tenant.core.web.TenantVisitContextInterceptor;
import com.focela.platform.web.config.WebProperties;
import com.focela.platform.web.core.handler.GlobalExceptionHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.BatchStrategies;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;
import lombok.RequiredArgsConstructor;

import java.util.*;

import static com.focela.platform.common.utils.collection.CollectionUtils.convertList;

@AutoConfiguration
@ConditionalOnProperty(prefix = "focela.tenant", value = "enable", matchIfMissing = true)
@EnableConfigurationProperties(TenantProperties.class)
@RequiredArgsConstructor
public class FocelaTenantAutoConfiguration {

        private final ApplicationContext applicationContext;

    @Bean
    public TenantFrameworkService tenantFrameworkService(TenantContractApi tenantApi) {
        return new DefaultTenantFrameworkService(tenantApi);
    }

    // ========== AOP ==========

    @Bean
    public TenantIgnoreAspect tenantIgnoreAspect() {
        return new TenantIgnoreAspect();
    }

    // ========== DB ==========

    @Bean
    public TenantLineInnerInterceptor tenantLineInnerInterceptor(TenantProperties properties,
                                                                 MybatisPlusInterceptor interceptor) {
        TenantLineInnerInterceptor inner = new TenantLineInnerInterceptor(new TenantDatabaseInterceptor(properties));
        // Add to the interceptor
        // It must be added first, mainly to be ahead of the pagination plugin. This is a MyBatis Plus requirement.
        MyBatisUtils.addInterceptor(interceptor, inner, 0);
        return inner;
    }

    // ========== WEB ==========

    @Bean
    public FilterRegistrationBean<TenantContextWebFilter> tenantContextWebFilter() {
        FilterRegistrationBean<TenantContextWebFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TenantContextWebFilter());
        registrationBean.setOrder(WebFilterOrderEnum.TENANT_CONTEXT_FILTER);
        return registrationBean;
    }

    @Bean
    public TenantVisitContextInterceptor tenantVisitContextInterceptor(TenantProperties tenantProperties,
                                                                       SecurityFrameworkService securityFrameworkService) {
        return new TenantVisitContextInterceptor(tenantProperties, securityFrameworkService);
    }

    @Bean
    public WebMvcConfigurer tenantWebMvcConfigurer(TenantProperties tenantProperties,
                                                   TenantVisitContextInterceptor tenantVisitContextInterceptor) {
        return new WebMvcConfigurer() {

            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(tenantVisitContextInterceptor)
                        .excludePathPatterns(tenantProperties.getIgnoreVisitUrls().toArray(new String[0]));
            }
        };
    }

    // ========== Security ==========

    @Bean
    public FilterRegistrationBean<TenantSecurityWebFilter> tenantSecurityWebFilter(TenantProperties tenantProperties,
                                                                                   WebProperties webProperties,
                                                                                   GlobalExceptionHandler globalExceptionHandler,
                                                                                   TenantFrameworkService tenantFrameworkService) {
        FilterRegistrationBean<TenantSecurityWebFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TenantSecurityWebFilter(webProperties, tenantProperties, getTenantIgnoreUrls(),
                globalExceptionHandler, tenantFrameworkService));
        registrationBean.setOrder(WebFilterOrderEnum.TENANT_SECURITY_FILTER);
        return registrationBean;
    }

    /**
     * If a Controller endpoint has the {@link TenantIgnore} annotation, add it to the set of tenant-ignored URLs.
     *
     * @return set of tenant-ignored URLs
     */
    private Set<String> getTenantIgnoreUrls() {
        Set<String> ignoreUrls = new HashSet<>();
        // Get the HandlerMethod collection of endpoints
        RequestMappingHandlerMapping requestMappingHandlerMapping = (RequestMappingHandlerMapping)
                applicationContext.getBean("requestMappingHandlerMapping");
        Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = requestMappingHandlerMapping.getHandlerMethods();
        // Find endpoints annotated with @TenantIgnore
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethodMap.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            if (!handlerMethod.hasMethodAnnotation(TenantIgnore.class) // method level
                && !handlerMethod.getBeanType().isAnnotationPresent(TenantIgnore.class)) { // class level
                continue;
            }
            // Add to the ignored URLs
            if (entry.getKey().getPatternsCondition() != null) {
                ignoreUrls.addAll(entry.getKey().getPatternsCondition().getPatterns());
            }
            if (entry.getKey().getPathPatternsCondition() != null) {
                ignoreUrls.addAll(
                        convertList(entry.getKey().getPathPatternsCondition().getPatterns(), PathPattern::getPatternString));
            }
        }
        return ignoreUrls;
    }

    // ========== MQ ==========

    @Bean
    public TenantRedisMessageInterceptor tenantRedisMessageInterceptor() {
        return new TenantRedisMessageInterceptor();
    }

    @Bean
    @ConditionalOnClass(name = "org.springframework.amqp.rabbit.core.RabbitTemplate")
    public TenantRabbitMQInitializer tenantRabbitMQInitializer() {
        return new TenantRabbitMQInitializer();
    }

    @Bean
    @ConditionalOnClass(name = "org.apache.rocketmq.spring.core.RocketMQTemplate")
    public TenantRocketMQInitializer tenantRocketMQInitializer() {
        return new TenantRocketMQInitializer();
    }

    @Bean
    @ConditionalOnClass(name = "org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory")
    public TenantKafkaConsumerInitializer tenantKafkaConsumerInitializer() {
        return new TenantKafkaConsumerInitializer();
    }

    // ========== Job ==========

    @Bean
    public TenantJobAspect tenantJobAspect(TenantFrameworkService tenantFrameworkService) {
        return new TenantJobAspect(tenantFrameworkService);
    }

    // ========== Redis ==========

    @Bean
    @Primary // when tenant is enabled, tenantRedisCacheManager is the primary Bean
    public RedisCacheManager tenantRedisCacheManager(RedisTemplate<String, Object> redisTemplate,
                                                     RedisCacheConfiguration redisCacheConfiguration,
                                                     FocelaCacheProperties focelaCacheProperties,
                                                     TenantProperties tenantProperties) {
        // Create RedisCacheWriter
        RedisConnectionFactory connectionFactory = Objects.requireNonNull(redisTemplate.getConnectionFactory());
        RedisCacheWriter cacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory,
                BatchStrategies.scan(focelaCacheProperties.getRedisScanBatchSize()));
        // Create TenantRedisCacheManager
        return new TenantRedisCacheManager(cacheWriter, redisCacheConfiguration, tenantProperties.getIgnoreCaches());
    }

}
