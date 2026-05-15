package com.focela.platform.system.api.logger.dto;

import com.fhs.core.trans.anno.Trans;
import com.fhs.core.trans.constant.TransType;
import com.fhs.core.trans.vo.VO;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * System operate log Resp DTO
 */
@Data
public class OperateLogRpcResponse implements VO {

    /**
     * Log ID
     */
    private Long id;
    /**
     * Trace ID
     */
    private String traceId;
    /**
     * User ID
     */
    @Trans(type = TransType.SIMPLE, targetClassName = "com.focela.platform.system.entity.user.UserEntity",
            fields = "nickname", ref = "userName")
    private Long userId;
    /**
     * User name
     */
    private String userName;
    /**
     * User type
     */
    private Integer userType;
    /**
     * Operation module type
     */
    private String type;
    /**
     * Operation name
     */
    private String subType;
    /**
     * Operation module business ID
     */
    private Long bizId;
    /**
     * Operation content
     */
    private String action;
    /**
     * Extension field
     */
    private String extra;

    /**
     * Request method name
     */
    private String requestMethod;
    /**
     * Request URL
     */
    private String requestUrl;
    /**
     * User IP
     */
    private String userIp;
    /**
     * Browser UA
     */
    private String userAgent;

    /**
     * Create time
     */
    private LocalDateTime createTime;

}
