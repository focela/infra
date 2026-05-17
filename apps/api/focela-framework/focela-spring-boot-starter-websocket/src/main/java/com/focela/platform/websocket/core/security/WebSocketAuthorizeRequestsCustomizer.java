package com.focela.platform.websocket.core.security;

import com.focela.platform.security.config.AuthorizeRequestsCustomizer;
import com.focela.platform.web.config.WebProperties;
import com.focela.platform.websocket.config.WebSocketProperties;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

/**
 * WebSocket authorization customizer.
 */
public class WebSocketAuthorizeRequestsCustomizer extends AuthorizeRequestsCustomizer {

    private final WebSocketProperties webSocketProperties;

    public WebSocketAuthorizeRequestsCustomizer(WebProperties webProperties,
                                                WebSocketProperties webSocketProperties) {
        super(webProperties);
        this.webSocketProperties = webSocketProperties;
    }

    @Override
    public void customize(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry.requestMatchers(webSocketProperties.getPath()).permitAll();
    }

}
