package com.focela.platform.framework.common.contract.infra.logger.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * API access log.
 */
@Data
public class ApiAccessLogCreateRpcRequest {

    /**
     * Trace ID.
     */
    private String traceId;
    /**
     * User ID.
     */
    private Long userId;
    /**
     * User type.
     */
    private Integer userType;
    /**
     * Application name.
     */
    @NotNull(message = "application name must not be blank")
    private String applicationName;

    /**
     * HTTP request method.
     */
    @NotNull(message = "HTTP method must not be blank")
    private String requestMethod;
    /**
     * Access URL.
     */
    @NotNull(message = "access URL must not be blank")
    private String requestUrl;
    /**
     * Request parameters.
     */
    private String requestParams;
    /**
     * Response body.
     */
    private String responseBody;
    /**
     * User IP.
     */
    @NotNull(message = "ip must not be blank")
    private String userIp;
    /**
     * Browser User-Agent.
     */
    @NotNull(message = "User-Agent must not be blank")
    private String userAgent;

    /**
     * Operation module.
     */
    private String operateModule;
    /**
     * Operation name.
     */
    private String operateName;
    /**
     * Operation category.
     *
     * Enum; see OperateTypeEnum.
     */
    private Integer operateType;

    /**
     * Request start time.
     */
    @NotNull(message = "start request time must not be blank")
    private LocalDateTime beginTime;
    /**
     * Request end time.
     */
    @NotNull(message = "end request time must not be blank")
    private LocalDateTime endTime;
    /**
     * Execution duration in milliseconds.
     */
    @NotNull(message = "execution duration must not be blank")
    private Integer duration;
    /**
     * Result code.
     */
    @NotNull(message = "error code must not be blank")
    private Integer resultCode;
    /**
     * Result message.
     */
    private String resultMsg;

}
