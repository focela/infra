package com.focela.platform.framework.common.business.infra.logger.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * API 错误日志
 */
@Data
public class ApiErrorLogCreateReqDTO {

    /**
     * 链路编号
     */
    private String traceId;
    /**
     * 账号编号
     */
    private Long userId;
    /**
     * 用户类型
     */
    private Integer userType;
    /**
     * 应用名
     */
    @NotNull(message = "application name must not be blank")
    private String applicationName;

    /**
     * 请求方法名
     */
    @NotNull(message = "HTTP method must not be blank")
    private String requestMethod;
    /**
     * 访问地址
     */
    @NotNull(message = "access URL must not be blank")
    private String requestUrl;
    /**
     * 请求参数
     */
    @NotNull(message = "request param must not be blank")
    private String requestParams;
    /**
     * 用户 IP
     */
    @NotNull(message = "ip must not be blank")
    private String userIp;
    /**
     * 浏览器 UA
     */
    @NotNull(message = "User-Agent must not be blank")
    private String userAgent;

    /**
     * 异常时间
     */
    @NotNull(message = "exception time must not be blank")
    private LocalDateTime exceptionTime;
    /**
     * 异常名
     */
    @NotNull(message = "exception name must not be blank")
    private String exceptionName;
    /**
     * 异常发生的类全名
     */
    @NotNull(message = "exception class FQN must not be blank")
    private String exceptionClassName;
    /**
     * 异常发生的类文件
     */
    @NotNull(message = "exception class file must not be blank")
    private String exceptionFileName;
    /**
     * 异常发生的方法名
     */
    @NotNull(message = "exception method name must not be blank")
    private String exceptionMethodName;
    /**
     * 异常发生的方法所在行
     */
    @NotNull(message = "exception line must not be blank")
    private Integer exceptionLineNumber;
    /**
     * 异常的栈轨迹异常的栈轨迹
     */
    @NotNull(message = "exception 栈轨迹must not be blank")
    private String exceptionStackTrace;
    /**
     * 异常导致的根消息
     */
    @NotNull(message = "root exception message must not be blank")
    private String exceptionRootCauseMessage;
    /**
     * 异常导致的消息
     */
    @NotNull(message = "exception message must not be blank")
    private String exceptionMessage;


}
