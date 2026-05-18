package com.focela.platform.infra.entity.job;

import com.focela.platform.mybatis.core.entity.BaseEntity;
import com.focela.platform.tenant.core.aop.TenantIgnore;
import com.focela.platform.infra.enums.job.JobStatusEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * Scheduled job Entity
 */
@TableName("infra_job")
@KeySequence("infra_job_seq") // Used for database primary key auto-increment in Oracle, PostgreSQL, Kingbase, DB2, H2. For databases like MySQL it can be omitted.
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
public class JobEntity extends BaseEntity {

    /**
     * Job ID
     */
    @TableId
    private Long id;
    /**
     * Job name
     */
    private String name;
    /**
     * Job status
     *
     * Enum {@link JobStatusEnum}
     */
    private Integer status;
    /**
     * Handler name
     */
    private String handlerName;
    /**
     * Handler parameter
     */
    private String handlerParam;
    /**
     * CRON expression
     */
    private String cronExpression;

    // ========== Retry related fields ==========
    /**
     * Retry count
     * Set to 0 if no retry
     */
    private Integer retryCount;
    /**
     * Retry interval, unit: milliseconds
     * Set to 0 if no interval
     */
    private Integer retryInterval;

    // ========== Monitor related fields ==========
    /**
     * Monitor timeout, unit: milliseconds
     * Empty means no monitoring
     *
     * Note, the purpose of the timeout here is not to cancel the task, but to alert when the task execution time is too long
     */
    private Integer monitorTimeout;

}
