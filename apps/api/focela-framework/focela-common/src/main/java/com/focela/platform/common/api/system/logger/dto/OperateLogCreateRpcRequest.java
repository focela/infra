package com.focela.platform.common.api.system.logger.dto;

import com.focela.platform.common.enums.UserTypeEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * System operate log Create Request DTO.
 */
@Data
public class OperateLogCreateRpcRequest {

    /**
     * Trace ID.
     *
     * The trace ID allows access logs, error logs, tracing logs, and logger output to be correlated for troubleshooting.
     */
    private String traceId;
    /**
     * User ID.
     *
     * References the id of MemberUserDO or UserEntity.
     */
    @NotNull(message = "user ID must not be blank")
    private Long userId;
    /**
     * User type.
     *
     * See {@link UserTypeEnum}.
     */
    @NotNull(message = "user type must not be blank")
    private Integer userType;
    /**
     * Operation module type.
     */
    @NotEmpty(message = "operation module type must not be blank")
    private String type;
    /**
     * Operation name.
     */
    @NotEmpty(message = "operation name must not be blank")
    private String subType;
    /**
     * Business ID of the operation module.
     */
    @NotNull(message = "operation module business ID must not be blank")
    private Long bizId;
    /**
     * Operation content, recording the details of the entire operation.
     * For example: modifying the user with ID 1, changing gender from male to female and name from A to B.
     */
    @NotEmpty(message = "operation content must not be blank")
    private String action;
    /**
     * Extension field; some complex business cases require additional fields recorded in JSON format.
     * For example, recording the order ID: { orderId: "1" }.
     */
    private String extra;

    /**
     * HTTP request method.
     */
    @NotEmpty(message = "HTTP method must not be blank")
    private String requestMethod;
    /**
     * Request URL.
     */
    @NotEmpty(message = "request URL must not be blank")
    private String requestUrl;
    /**
     * User IP.
     */
    @NotEmpty(message = "user IP must not be blank")
    private String userIp;
    /**
     * Browser User-Agent.
     */
    @NotEmpty(message = "user agent must not be blank")
    private String userAgent;

}
