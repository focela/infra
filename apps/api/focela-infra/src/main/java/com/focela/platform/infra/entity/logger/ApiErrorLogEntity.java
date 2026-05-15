package com.focela.platform.infra.entity.logger;

import com.focela.platform.framework.common.enums.UserTypeEnum;
import com.focela.platform.framework.mybatis.core.entity.BaseEntity;
import com.focela.platform.infra.enums.logger.ApiErrorLogProcessStatusEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * API exception data
 */
@TableName("infra_api_error_log")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@KeySequence(value = "infra_api_error_log_seq")
public class ApiErrorLogEntity extends BaseEntity {

    /**
     * Maximum length of {@link #requestParams}
     */
    public static final Integer REQUEST_PARAMS_MAX_LENGTH = 8000;

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * User ID
     */
    private Long userId;
    /**
     * Trace ID
     *
     * Generally, by trace ID, access logs, error logs, trace logs, logger logs, etc. can be combined to troubleshoot.
     */
    private String traceId;
    /**
     * User type
     *
     * Enum {@link UserTypeEnum}
     */
    private Integer userType;
    /**
     * Application name
     *
     * Currently reads spring.application.name
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
     * User IP
     */
    private String userIp;
    /**
     * Browser UA
     */
    private String userAgent;

    // ========== Exception related fields ==========

    /**
     * Exception occurrence time
     */
    private LocalDateTime exceptionTime;
    /**
     * Exception name
     *
     * Full class name of {@link Throwable#getClass()}
     */
    private String exceptionName;
    /**
     * Exception message
     *
     * {@link cn.hutool.core.exceptions.ExceptionUtil#getMessage(Throwable)}
     */
    private String exceptionMessage;
    /**
     * Exception root cause message
     *
     * {@link cn.hutool.core.exceptions.ExceptionUtil#getRootCauseMessage(Throwable)}
     */
    private String exceptionRootCauseMessage;
    /**
     * Exception stack trace
     *
     * {@link org.apache.commons.lang3.exception.ExceptionUtils#getStackTrace(Throwable)}
     */
    private String exceptionStackTrace;
    /**
     * Full class name where the exception occurred
     *
     * {@link StackTraceElement#getClassName()}
     */
    private String exceptionClassName;
    /**
     * Class file where the exception occurred
     *
     * {@link StackTraceElement#getFileName()}
     */
    private String exceptionFileName;
    /**
     * Method name where the exception occurred
     *
     * {@link StackTraceElement#getMethodName()}
     */
    private String exceptionMethodName;
    /**
     * Line number where the exception occurred
     *
     * {@link StackTraceElement#getLineNumber()}
     */
    private Integer exceptionLineNumber;

    // ========== Process related fields ==========

    /**
     * Process status
     *
     * Enum {@link ApiErrorLogProcessStatusEnum}
     */
    private Integer processStatus;
    /**
     * Process time
     */
    private LocalDateTime processTime;
    /**
     * Process user ID
     *
     * Associated with com.focela.platform.adminserver.modules.system.repository.entity.user.SysUserDO.SysUserDO#getId()
     */
    private Long processUserId;

}
