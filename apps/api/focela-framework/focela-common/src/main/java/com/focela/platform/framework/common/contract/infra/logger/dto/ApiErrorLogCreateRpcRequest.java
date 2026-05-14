package com.focela.platform.framework.common.contract.infra.logger.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * API error log.
 */
@Data
public class ApiErrorLogCreateRpcRequest {

    /**
     * Trace ID.
     */
    private String traceId;
    /**
     * Account ID.
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
    @NotNull(message = "request param must not be blank")
    private String requestParams;
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
     * Exception time.
     */
    @NotNull(message = "exception time must not be blank")
    private LocalDateTime exceptionTime;
    /**
     * Exception name.
     */
    @NotNull(message = "exception name must not be blank")
    private String exceptionName;
    /**
     * Fully qualified class name where the exception occurred.
     */
    @NotNull(message = "exception class FQN must not be blank")
    private String exceptionClassName;
    /**
     * Source file where the exception occurred.
     */
    @NotNull(message = "exception class file must not be blank")
    private String exceptionFileName;
    /**
     * Method name where the exception occurred.
     */
    @NotNull(message = "exception method name must not be blank")
    private String exceptionMethodName;
    /**
     * Line number where the exception occurred.
     */
    @NotNull(message = "exception line must not be blank")
    private Integer exceptionLineNumber;
    /**
     * Stack trace of the exception.
     */
    @NotNull(message = "exception stack trace must not be blank")
    private String exceptionStackTrace;
    /**
     * Root cause message of the exception.
     */
    @NotNull(message = "root exception message must not be blank")
    private String exceptionRootCauseMessage;
    /**
     * Exception message.
     */
    @NotNull(message = "exception message must not be blank")
    private String exceptionMessage;


}
