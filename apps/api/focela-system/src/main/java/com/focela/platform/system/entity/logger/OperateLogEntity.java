package com.focela.platform.system.entity.logger;

import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.mybatis.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Operate log table
 */
@TableName(value = "system_operate_log", autoResultMap = true)
@KeySequence("system_operate_log_seq") // Primary key auto-increment for databases such as Oracle, PostgreSQL, Kingbase, DB2, H2. Can be omitted for databases like MySQL.
@Data
public class OperateLogEntity extends BaseEntity {

    /**
     * Log primary key
     */
    @TableId
    private Long id;
    /**
     * Trace ID
     *
     * In general, the trace ID can correlate access logs, error logs, trace logs, logger output, etc., for troubleshooting.
     */
    private String traceId;
    /**
     * User ID
     *
     * Associated with member user ID or {@link com.focela.platform.system.entity.user.UserEntity#getId()}.
     */
    private Long userId;
    /**
     * User type
     *
     * Associated with {@link  UserTypeEnum}
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
     * Business ID of the operation module
     */
    private Long bizId;
    /**
     * Log content, records the details of the whole operation
     *
     * For example, updating user info with ID 1: change gender from male to female, change name from "Foo" to "Bar".
     */
    private String action;
    /**
     * Extended field. Some complex businesses need to record extra fields (JSON format).
     *
     * For example, recording an order ID: { orderId: "1"}
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

}
