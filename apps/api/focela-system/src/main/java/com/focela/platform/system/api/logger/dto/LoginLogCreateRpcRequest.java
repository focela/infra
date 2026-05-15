package com.focela.platform.system.api.logger.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Login log create Request DTO
 */
@Data
public class LoginLogCreateRpcRequest {

    /**
     * Log type
     */
    @NotNull(message = "log type must not be blank")
    private Integer logType;
    /**
     * Trace ID
     */
    private String traceId;

    /**
     * User ID
     */
    private Long userId;
    /**
     * User type
     */
    @NotNull(message = "user type must not be blank")
    private Integer userType;
    /**
     * User account
     *
     * No longer requires username to be non-empty, because during Member social login there is temporarily no username (mobile)!
     */
    private String username;

    /**
     * Login result
     */
    @NotNull(message = "login result must not be blank")
    private Integer result;

    /**
     * User IP
     */
    @NotEmpty(message = "user IP must not be blank")
    private String userIp;
    /**
     * Browser UserAgent
     *
     * Allowed to be empty, reason: when Job triggers expired logout, the UserAgent cannot be passed
     */
    private String userAgent;

}
