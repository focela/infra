package com.focela.platform.module.infra.config.monitor;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * Spring Boot Admin Server configuration
 *
 * Contains the Admin Server enablement config and security config.
 * The security config is independent of {@link com.focela.platform.framework.security.config.FocelaWebSecurityConfigurerAdapter},
 * using HTTP Basic authentication to protect Admin Server endpoints, without affecting the existing Token authentication mechanism
 */
@Configuration(proxyBeanMethods = false)
@EnableAdminServer
@ConditionalOnClass(name = "de.codecentric.boot.admin.server.config.AdminServerProperties") // Purpose: start spring boot admin monitoring service on demand
public class AdminServerConfiguration {

    @Value("${spring.boot.admin.context-path:''}")
    private String adminSeverContextPath;

    @Value("${spring.boot.admin.client.username:admin}")
    private String username;

    @Value("${spring.boot.admin.client.password:admin}")
    private String password;

    /**
     * Spring Boot Admin dedicated InMemoryUserDetailsManager
     * Uses in-memory storage, isolated from system users
     */
    @Bean("adminUserDetailsManager")
    public InMemoryUserDetailsManager adminUserDetailsManager(PasswordEncoder passwordEncoder) {
        UserDetails adminUser = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles("ADMIN_SERVER")
                .build();
        return new InMemoryUserDetailsManager(adminUser);
    }

    /**
     * Spring Boot Admin Server SecurityFilterChain
     * Uses @Order(1) to ensure priority over the default SecurityFilterChain
     */
    @Bean("adminServerSecurityFilterChain")
    @Order(1)
    public SecurityFilterChain adminServerSecurityFilterChain(HttpSecurity httpSecurity,
                                                               InMemoryUserDetailsManager adminUserDetailsManager) throws Exception {
        // Handler after successful login
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");
        successHandler.setDefaultTargetUrl(adminSeverContextPath + "/");

        // Configure HttpSecurity object
        httpSecurity
                // Match only Admin Server paths
                .securityMatcher(adminSeverContextPath + "/**")
                // Use independent UserDetailsManager
                .userDetailsService(adminUserDetailsManager)
                // Authorization config
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(adminSeverContextPath + "/assets/**").permitAll() // Static resources allow anonymous access
                        .requestMatchers(adminSeverContextPath + "/login").permitAll() // Login page allows anonymous access
                        .dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll() // Allow async requests
                        .anyRequest().authenticated() // Other requests require authentication
                )
                // Form login config (for Admin UI access)
                .formLogin(form -> form
                        .loginPage(adminSeverContextPath + "/login")
                        .successHandler(successHandler)
                        .permitAll()
                )
                // Logout config
                .logout(logout -> logout
                        .logoutUrl(adminSeverContextPath + "/logout")
                        .logoutSuccessUrl(adminSeverContextPath + "/login")
                )
                // HTTP Basic authentication (for Admin Client registration)
                .httpBasic(Customizer.withDefaults())
                // CSRF config
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers(
                                adminSeverContextPath + "/instances", // Admin Client registration endpoint ignores CSRF
                                adminSeverContextPath + "/actuator/**" // Actuator endpoints ignore CSRF
                        )
                );
        return httpSecurity.build();
    }

}
