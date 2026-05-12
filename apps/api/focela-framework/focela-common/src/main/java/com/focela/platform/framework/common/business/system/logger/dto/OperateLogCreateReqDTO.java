package com.focela.platform.framework.common.business.system.logger.dto;

import com.focela.platform.framework.common.enums.UserTypeEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 系统操作日志 Create Request DTO
 */
@Data
public class OperateLogCreateReqDTO {

    /**
     * 链路追踪编号
     *
     * 一般来说，通过链路追踪编号，可以将访问日志，错误日志，链路追踪日志，logger 打印日志等，结合在一起，从而进行排错。
     */
    private String traceId;
    /**
     * 用户编号
     *
     * 关联 MemberUserDO 的 id 属性，或者 AdminUserEntity 的 id 属性
     */
    @NotNull(message = "user ID must not be blank")
    private Long userId;
    /**
     * 用户类型
     *
     * 关联 {@link  UserTypeEnum}
     */
    @NotNull(message = "user type must not be blank")
    private Integer userType;
    /**
     * 操作模块类型
     */
    @NotEmpty(message = "operation 模block type must not be blank")
    private String type;
    /**
     * 操作名
     */
    @NotEmpty(message = "operation 名must not be blank")
    private String subType;
    /**
     * 操作模块业务编号
     */
    @NotNull(message = "operation 模block 业务ID must not be blank")
    private Long bizId;
    /**
     * 操作内容，记录整个操作的明细
     * 例如说，修改编号为 1 的用户信息，将性别从男改成女，将姓名从芋道改成源码。
     */
    @NotEmpty(message = "operation content must not be blank")
    private String action;
    /**
     * 拓展字段，有些复杂的业务，需要记录一些字段 ( JSON 格式 )
     * 例如说，记录订单编号，{ orderId: "1"}
     */
    private String extra;

    /**
     * 请求方法名
     */
    @NotEmpty(message = "HTTP method must not be blank")
    private String requestMethod;
    /**
     * 请求地址
     */
    @NotEmpty(message = "request URL must not be blank")
    private String requestUrl;
    /**
     * 用户 IP
     */
    @NotEmpty(message = "user IP must not be blank")
    private String userIp;
    /**
     * 浏览器 UA
     */
    @NotEmpty(message = "user agent must not be blank")
    private String userAgent;

}
