package com.focela.platform.framework.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

@ConfigurationProperties(prefix = "focela.security")
@Validated
@Data
public class SecurityProperties {

    /**
     * Request Header carrying the access token in HTTP requests
     */
    @NotEmpty(message = "Token Header must not be blank")
    private String tokenHeader = "Authorization";
    /**
     * Request parameter carrying the access token in HTTP requests.
     *
     * Original purpose: handle WebSocket which cannot pass parameters via headers and must use a token query parameter.
     */
    @NotEmpty(message = "Token Parameter must not be blank")
    private String tokenParameter = "token";

    /**
     * Switch for mock mode
     */
    @NotNull(message = "mock mode switch must not be blank")
    private Boolean mockEnable = false;
    /**
     * Secret for mock mode.
     * A secret must be configured to ensure security.
     */
    @NotEmpty(message = "mock mode secret must not be blank") // A default value is set here because it is actually only required when mockEnable is true.
    private String mockSecret = "test";

    /**
     * URL list that requires no login
     */
    private List<String> permitAllUrls = Collections.emptyList();

    /**
     * PasswordEncoder encoding strength; higher means more CPU overhead
     */
    private Integer passwordEncoderLength = 4;
}
