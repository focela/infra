package com.focela.platform.security.config;

import com.focela.platform.security.core.filter.TokenAuthenticationFilter;
import com.focela.platform.web.config.WebProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class FocelaSecurityFilterChainAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    SecurityAutoConfiguration.class,
                    SecurityFilterAutoConfiguration.class,
                    FocelaSecurityFilterChainAutoConfiguration.class))
            .withUserConfiguration(TestSupportConfiguration.class);

    @Test
    void providesSecurityFilterChain() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(FocelaSecurityFilterChainAutoConfiguration.class);
            assertThat(context).hasSingleBean(SecurityFilterChain.class);
        });
    }

    @Configuration(proxyBeanMethods = false)
    static class TestSupportConfiguration {

        @Bean
        WebProperties webProperties() {
            WebProperties webProperties = new WebProperties();
            webProperties.setAdminUi(new WebProperties.Ui());
            return webProperties;
        }

        @Bean
        SecurityProperties securityProperties() {
            return new SecurityProperties();
        }

        @Bean
        AuthenticationEntryPoint authenticationEntryPoint() {
            return mock(AuthenticationEntryPoint.class);
        }

        @Bean
        AccessDeniedHandler accessDeniedHandler() {
            return mock(AccessDeniedHandler.class);
        }

        @Bean
        TokenAuthenticationFilter authenticationTokenFilter() {
            return mock(TokenAuthenticationFilter.class);
        }

        @Bean("requestMappingHandlerMapping")
        RequestMappingHandlerMapping requestMappingHandlerMapping() {
            return new RequestMappingHandlerMapping();
        }

        @Bean("mvcHandlerMappingIntrospector")
        HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
            return new HandlerMappingIntrospector();
        }
    }
}
