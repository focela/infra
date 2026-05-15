package com.focela.platform.infra.entity.job;

import com.focela.platform.mybatis.core.entity.BaseEntity;
import com.focela.platform.quartz.core.handler.JobHandler;
import com.focela.platform.tenant.core.aop.TenantIgnore;
import com.focela.platform.infra.enums.job.JobLogStatusEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Scheduled job execution log
 */
@TableName("infra_job_log")
@KeySequence("infra_job_log_seq") // Used for database primary key auto-increment in Oracle, PostgreSQL, Kingbase, DB2, H2. For databases like MySQL it can be omitted.
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
public class JobLogEntity extends BaseEntity {

    /**
     * Log ID
     */
    private Long id;
    /**
     * Job ID
     *
     * Associated with {@link JobEntity#getId()}
     */
    private Long jobId;
    /**
     * Handler name
     *
     * Redundant field {@link JobEntity#getHandlerName()}
     */
    private String handlerName;
    /**
     * Handler parameter
     *
     * Redundant field {@link JobEntity#getHandlerParam()}
     */
    private String handlerParam;
    /**
     * Execution index
     *
     * Used to distinguish whether it's a retry execution. If it's a retry execution, index is greater than 1
     */
    private Integer executeIndex;

    /**
     * Execution begin time
     */
    private LocalDateTime beginTime;
    /**
     * Execution end time
     */
    private LocalDateTime endTime;
    /**
     * Execution duration, unit: milliseconds
     */
    private Integer duration;
    /**
     * Status
     *
     * Enum {@link JobLogStatusEnum}
     */
    private Integer status;
    /**
     * Result data
     *
     * On success, uses the result of {@link JobHandler#execute(String)}
     * On failure, uses the exception stack trace of {@link JobHandler#execute(String)}
     */
    private String result;

}
