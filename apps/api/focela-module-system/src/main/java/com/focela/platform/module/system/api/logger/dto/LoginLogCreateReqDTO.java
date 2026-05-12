package com.focela.platform.module.system.api.logger.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 登录日志创建 Request DTO
 */
@Data
public class LoginLogCreateReqDTO {

    /**
     * 日志类型
     */
    @NotNull(message = "log type must not be blank")
    private Integer logType;
    /**
     * 链路追踪编号
     */
    private String traceId;

    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 用户类型
     */
    @NotNull(message = "user type must not be blank")
    private Integer userType;
    /**
     * 用户账号
     *
     * 不再强制校验 username 非空，因为 Member 社交登录时，此时暂时没有 username(mobile）！
     */
    private String username;

    /**
     * 登录结果
     */
    @NotNull(message = "login 结果must not be blank")
    private Integer result;

    /**
     * 用户 IP
     */
    @NotEmpty(message = "user IP must not be blank")
    private String userIp;
    /**
     * 浏览器 UserAgent
     *
     * 允许空，原因：Job 过期登出时，是无法传递 UserAgent 的
     */
    private String userAgent;

}
