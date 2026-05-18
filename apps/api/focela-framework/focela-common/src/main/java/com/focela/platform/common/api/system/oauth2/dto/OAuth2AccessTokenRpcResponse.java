package com.focela.platform.common.api.system.oauth2.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * OAuth2.0 access token info response.
 */
@Data
public class OAuth2AccessTokenRpcResponse implements Serializable {

    /**
     * Access token.
     */
    private String accessToken;
    /**
     * Refresh token.
     */
    private String refreshToken;
    /**
     * User ID.
     */
    private Long userId;
    /**
     * User type.
     */
    private Integer userType;
    /**
     * Expiration time.
     */
    private LocalDateTime expiresTime;

}
