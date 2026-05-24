package com.focela.platform.security.config;

import com.focela.platform.security.core.filter.TokenAuthenticationFilter;
import com.focela.platform.web.config.WebProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.util.List;

/**
 * Spring Security filter-chain auto-configuration.
 */
@AutoConfiguration
@AutoConfigureOrder(-1)
@EnableMethodSecurity(securedEnabled = true)
@SuppressWarnings("deprecation")
public class FocelaSecurityFilterChainAutoConfiguration extends FocelaWebSecurityConfigurerAdapter {

    public FocelaSecurityFilterChainAutoConfiguration(WebProperties webProperties,
                                                      SecurityProperties securityProperties,
                                                      AuthenticationEntryPoint authenticationEntryPoint,
                                                      AccessDeniedHandler accessDeniedHandler,
                                                      TokenAuthenticationFilter authenticationTokenFilter,
                                                      List<AuthorizeRequestsCustomizer> authorizeRequestsCustomizers,
                                                      ApplicationContext applicationContext) {
        super(webProperties, securityProperties, authenticationEntryPoint, accessDeniedHandler, authenticationTokenFilter,
                authorizeRequestsCustomizers, applicationContext);
    }

}
