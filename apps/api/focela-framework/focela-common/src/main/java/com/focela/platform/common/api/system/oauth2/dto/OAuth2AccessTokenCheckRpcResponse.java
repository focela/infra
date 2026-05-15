package com.focela.platform.common.api.system.oauth2.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * OAuth2.0 access token validation Response DTO.
 */
@Data
public class OAuth2AccessTokenCheckRpcResponse implements Serializable {

    /**
     * User ID.
     */
    private Long userId;
    /**
     * User type.
     */
    private Integer userType;
    /**
     * User info.
     */
    private Map<String, String> userInfo;
    /**
     * Tenant ID.
     */
    private Long tenantId;
    /**
     * Authorization scope array.
     */
    private List<String> scopes;
    /**
     * Expiration time.
     */
    private LocalDateTime expiresTime;

}
