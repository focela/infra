package com.focela.platform.framework.security.config;

import com.focela.platform.framework.common.api.system.oauth2.OAuth2TokenContractApi;
import com.focela.platform.framework.common.api.system.permission.PermissionContractApi;
import com.focela.platform.framework.security.core.context.TransmittableThreadLocalSecurityContextHolderStrategy;
import com.focela.platform.framework.security.core.filter.TokenAuthenticationFilter;
import com.focela.platform.framework.security.core.handler.JsonAccessDeniedHandler;
import com.focela.platform.framework.security.core.handler.JsonAuthenticationEntryPoint;
import com.focela.platform.framework.security.core.service.SecurityFrameworkService;
import com.focela.platform.framework.security.core.service.DefaultSecurityFrameworkService;
import com.focela.platform.framework.web.core.handler.GlobalExceptionHandler;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * Spring Security auto-configuration class, mainly used to configure related components.
 *
 * Note: this must not be combined with {@link FocelaWebSecurityConfigurerAdapter}; doing so causes initialization errors.
 * See https://stackoverflow.com/questions/53847050/spring-boot-delegatebuilder-cannot-be-null-on-autowiring-authenticationmanager
 */
@AutoConfiguration
@AutoConfigureOrder(-1) // Purpose: run before Spring Security auto-configuration so that, after a one-click package rename, the org.* base packages still take effect
@EnableConfigurationProperties(SecurityProperties.class)
public class FocelaSecurityAutoConfiguration {

    @Resource
    private SecurityProperties securityProperties;

    /**
     * Authentication-failure handler Bean
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new JsonAuthenticationEntryPoint();
    }

    /**
     * Insufficient-permission handler Bean
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new JsonAccessDeniedHandler();
    }

    /**
     * Spring Security password encoder.
     * For security reasons, BCryptPasswordEncoder is used.
     *
     * @see <a href="http://stackabuse.com/password-encoding-with-spring-security/">Password Encoding with Spring Security</a>
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(securityProperties.getPasswordEncoderLength());
    }

    /**
     * Token authentication filter Bean
     */
    @Bean
    public TokenAuthenticationFilter authenticationTokenFilter(GlobalExceptionHandler globalExceptionHandler,
                                                               OAuth2TokenContractApi oauth2TokenApi) {
        return new TokenAuthenticationFilter(securityProperties, globalExceptionHandler, oauth2TokenApi);
    }

    @Bean("ss") // Use Spring Security's abbreviation for convenience
    public SecurityFrameworkService securityFrameworkService(PermissionContractApi permissionApi) {
        return new DefaultSecurityFrameworkService(permissionApi);
    }

    /**
     * Declare invocation of {@link SecurityContextHolder#setStrategyName(String)}
     * to configure {@link TransmittableThreadLocalSecurityContextHolderStrategy} as the Security context strategy.
     */
    @Bean
    public MethodInvokingFactoryBean securityContextHolderMethodInvokingFactoryBean() {
        MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
        methodInvokingFactoryBean.setTargetClass(SecurityContextHolder.class);
        methodInvokingFactoryBean.setTargetMethod("setStrategyName");
        methodInvokingFactoryBean.setArguments(TransmittableThreadLocalSecurityContextHolderStrategy.class.getName());
        return methodInvokingFactoryBean;
    }

}
