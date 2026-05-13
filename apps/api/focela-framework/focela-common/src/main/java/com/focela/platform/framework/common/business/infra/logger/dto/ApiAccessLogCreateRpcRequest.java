package com.focela.platform.framework.common.business.infra.logger.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * API 访问日志
 */
@Data
public class ApiAccessLogCreateRpcRequest {

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
    private String requestParams;
    /**
     * 响应结果
     */
    private String responseBody;
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
     * 操作模块
     */
    private String operateModule;
    /**
     * 操作名
     */
    private String operateName;
    /**
     * 操作分类
     *
     * 枚举，参见 OperateTypeEnum 类
     */
    private Integer operateType;

    /**
     * 开始请求时间
     */
    @NotNull(message = "start request time must not be blank")
    private LocalDateTime beginTime;
    /**
     * 结束请求时间
     */
    @NotNull(message = "end request time must not be blank")
    private LocalDateTime endTime;
    /**
     * 执行时长，单位：毫秒
     */
    @NotNull(message = "execution duration must not be blank")
    private Integer duration;
    /**
     * 结果码
     */
    @NotNull(message = "error code must not be blank")
    private Integer resultCode;
    /**
     * 结果提示
     */
    private String resultMsg;

}
