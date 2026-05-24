package com.focela.platform.security.config;

import cn.hutool.core.collection.CollUtil;
import com.focela.platform.security.core.filter.TokenAuthenticationFilter;
import com.focela.platform.web.config.WebProperties;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.DispatcherType;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.focela.platform.common.utils.collection.CollectionUtils.convertList;

/**
 * Custom Spring Security configuration adapter implementation.
 *
 * @deprecated use {@link FocelaSecurityFilterChainAutoConfiguration}. This class remains as the
 * compatibility superclass for the auto-configuration entry point.
 */
@Deprecated(since = "1.0.0", forRemoval = false)
@AutoConfiguration
@AutoConfigureOrder(-1) // Purpose: run before Spring Security auto-configuration so that, after a one-click package rename, the org.* base packages still take effect
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class FocelaWebSecurityConfigurerAdapter {

    private final WebProperties webProperties;
    private final SecurityProperties securityProperties;

    /**
     * Authentication-failure handler Bean
     */
    private final AuthenticationEntryPoint authenticationEntryPoint;
    /**
     * Insufficient-permission handler Bean
     */
    private final AccessDeniedHandler accessDeniedHandler;
    /**
     * Token authentication filter Bean
     */
    private final TokenAuthenticationFilter authenticationTokenFilter;

    /**
     * Custom permission mapping Beans
     *
     * @see #filterChain(HttpSecurity)
     */
    private final List<AuthorizeRequestsCustomizer> authorizeRequestsCustomizers;

    private final ApplicationContext applicationContext;

    /**
     * Spring Security does not declare @Bean when creating AuthenticationManager, so it cannot be injected.
     * Override this method and annotate it with @Bean to resolve the issue.
     */
    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Configure URL security.
     *
     * anyRequest          |   matches all request paths
     * access              |   accessible when the SpringEL expression evaluates to true
     * anonymous           |   anonymous users may access
     * denyAll             |   users may not access
     * fullyAuthenticated  |   accessible after full authentication (not via remember-me auto-login)
     * hasAnyAuthority     |   accessible if the user has any of the given authorities
     * hasAnyRole          |   accessible if the user has any of the given roles
     * hasAuthority        |   accessible if the user has the given authority
     * hasIpAddress        |   accessible if the user's IP matches the given parameter
     * hasRole             |   accessible if the user has the given role
     * permitAll           |   any user may access
     * rememberMe          |   users authenticated via remember-me may access
     * authenticated       |   accessible after login
     */
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        // Logout
        httpSecurity
                // Enable CORS
                .cors(Customizer.withDefaults())
                // Disable CSRF because Sessions are not used
                .csrf(AbstractHttpConfigurer::disable)
                // Token-based, so no Session needed
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(c -> c.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                // Pile of custom Spring Security handlers
                .exceptionHandling(c -> c.authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler));
        // Login is not implemented via Spring Security extension points for now; supporting multiple user types
        // and multiple login methods is relatively complex and the learning curve is high.

        // Get the URL list contributed by @PermitAll (no login required)
        Multimap<HttpMethod, String> permitAllUrls = getPermitAllUrlsFromAnnotations();
        // Configure permissions for each request
        httpSecurity
                // (1) Global shared rules
                .authorizeHttpRequests(c -> c
                    // 1.1 Static resources, anonymous access allowed
                    .requestMatchers(HttpMethod.GET, "/*.html", "/*.css", "/*.js").permitAll()
                    // 1.2 @PermitAll: no authentication required
                    .requestMatchers(HttpMethod.GET, permitAllUrls.get(HttpMethod.GET).toArray(new String[0])).permitAll()
                    .requestMatchers(HttpMethod.POST, permitAllUrls.get(HttpMethod.POST).toArray(new String[0])).permitAll()
                    .requestMatchers(HttpMethod.PUT, permitAllUrls.get(HttpMethod.PUT).toArray(new String[0])).permitAll()
                    .requestMatchers(HttpMethod.DELETE, permitAllUrls.get(HttpMethod.DELETE).toArray(new String[0])).permitAll()
                    .requestMatchers(HttpMethod.HEAD, permitAllUrls.get(HttpMethod.HEAD).toArray(new String[0])).permitAll()
                    .requestMatchers(HttpMethod.PATCH, permitAllUrls.get(HttpMethod.PATCH).toArray(new String[0])).permitAll()
                    // 1.3 permit-all URLs from focela.security.permit-all-urls
                    .requestMatchers(securityProperties.getPermitAllUrls().toArray(new String[0])).permitAll()
                )
                // (2) Per-project custom rules
                .authorizeHttpRequests(c -> authorizeRequestsCustomizers.forEach(customizer -> customizer.customize(c)))
                // (3) Fallback rule: authentication required
                .authorizeHttpRequests(c -> c
                        .dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll() // WebFlux async requests require no authentication; purpose: SSE scenarios
                        .anyRequest().authenticated());

        // Add Token Filter
        httpSecurity.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    private String buildAppApi(String url) {
        return webProperties.getAppApi().getPrefix() + url;
    }

    private Multimap<HttpMethod, String> getPermitAllUrlsFromAnnotations() {
        Multimap<HttpMethod, String> result = HashMultimap.create();
        // Get the HandlerMethod collection for endpoints
        RequestMappingHandlerMapping requestMappingHandlerMapping = (RequestMappingHandlerMapping)
                applicationContext.getBean("requestMappingHandlerMapping");
        Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = requestMappingHandlerMapping.getHandlerMethods();
        // Get endpoints annotated with @PermitAll
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethodMap.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            if (!handlerMethod.hasMethodAnnotation(PermitAll.class) // method level
                && !handlerMethod.getBeanType().isAnnotationPresent(PermitAll.class)) { // class level
                continue;
            }
            Set<String> urls = new HashSet<>();
            if (entry.getKey().getPatternsCondition() != null) {
                urls.addAll(entry.getKey().getPatternsCondition().getPatterns());
            }
            if (entry.getKey().getPathPatternsCondition() != null) {
                urls.addAll(convertList(entry.getKey().getPathPatternsCondition().getPatterns(), PathPattern::getPatternString));
            }
            if (urls.isEmpty()) {
                continue;
            }

            // Special case: when @RequestMapping is used without a method attribute, treat it as login-free for all methods
            Set<RequestMethod> methods = entry.getKey().getMethodsCondition().getMethods();
            if (CollUtil.isEmpty(methods)) {
                result.putAll(HttpMethod.GET, urls);
                result.putAll(HttpMethod.POST, urls);
                result.putAll(HttpMethod.PUT, urls);
                result.putAll(HttpMethod.DELETE, urls);
                result.putAll(HttpMethod.HEAD, urls);
                result.putAll(HttpMethod.PATCH, urls);
                continue;
            }
            // Add to result based on request method
            entry.getKey().getMethodsCondition().getMethods().forEach(requestMethod -> {
                switch (requestMethod) {
                    case GET:
                        result.putAll(HttpMethod.GET, urls);
                        break;
                    case POST:
                        result.putAll(HttpMethod.POST, urls);
                        break;
                    case PUT:
                        result.putAll(HttpMethod.PUT, urls);
                        break;
                    case DELETE:
                        result.putAll(HttpMethod.DELETE, urls);
                        break;
                    case HEAD:
                        result.putAll(HttpMethod.HEAD, urls);
                        break;
                    case PATCH:
                        result.putAll(HttpMethod.PATCH, urls);
                        break;
                }
            });
        }
        return result;
    }

}
