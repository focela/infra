package com.focela.platform.module.infra.entity.logger;

import com.focela.platform.framework.apilog.core.enums.OperateTypeEnum;
import com.focela.platform.framework.common.enums.UserTypeEnum;
import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.framework.mybatis.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * API access log
 */
@TableName("infra_api_access_log")
@KeySequence(value = "infra_api_access_log_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiAccessLogEntity extends BaseEntity {

    /**
     * Maximum length of {@link #requestParams}
     */
    public static final Integer REQUEST_PARAMS_MAX_LENGTH = 8000;

    /**
     * Maximum length of {@link #resultMsg}
     */
    public static final Integer RESULT_MSG_MAX_LENGTH = 512;

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * Trace ID
     *
     * Generally, by trace ID, access logs, error logs, trace logs, logger logs, etc. can be combined to troubleshoot.
     */
    private String traceId;
    /**
     * User ID
     */
    private Long userId;
    /**
     * User type
     *
     * Enum {@link UserTypeEnum}
     */
    private Integer userType;
    /**
     * Application name
     *
     * Currently reads `spring.application.name` config item
     */
    private String applicationName;

    // ========== Request related fields ==========

    /**
     * Request method name
     */
    private String requestMethod;
    /**
     * Request URL
     */
    private String requestUrl;
    /**
     * Request parameters
     *
     * query: Query String
     * body: Quest Body
     */
    private String requestParams;
    /**
     * Response body
     */
    private String responseBody;
    /**
     * User IP
     */
    private String userIp;
    /**
     * Browser UA
     */
    private String userAgent;

    // ========== Execution related fields ==========

    /**
     * Operation module
     */
    private String operateModule;
    /**
     * Operation name
     */
    private String operateName;
    /**
     * Operation category
     *
     * Enum {@link OperateTypeEnum}
     */
    private Integer operateType;

    /**
     * Request begin time
     */
    private LocalDateTime beginTime;
    /**
     * Request end time
     */
    private LocalDateTime endTime;
    /**
     * Execution duration, unit: milliseconds
     */
    private Integer duration;

    /**
     * Result code
     *
     * Currently uses the {@link CommonResult#getCode()} property
     */
    private Integer resultCode;
    /**
     * Result message
     *
     * Currently uses the {@link CommonResult#getMsg()} property
     */
    private String resultMsg;

}
