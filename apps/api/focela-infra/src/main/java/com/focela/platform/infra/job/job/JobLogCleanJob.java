package com.focela.platform.infra.job.job;

import com.focela.platform.framework.quartz.core.handler.JobHandler;
import com.focela.platform.framework.tenant.core.aop.TenantIgnore;
import com.focela.platform.infra.service.job.JobLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import jakarta.annotation.Resource;

/**
 * 物理删除 N 天前的任务日志的 Job
 */
@Slf4j
@Component
public class JobLogCleanJob implements JobHandler {

    @Resource
    private JobLogService jobLogService;

    /**
     * 清理超过（14）天的日志
     */
    private static final Integer JOB_CLEAN_RETAIN_DAY = 14;

    /**
     * 每次删除间隔的条数，如果值太高可能会造成数据库的压力过大
     */
    private static final Integer DELETE_LIMIT = 100;

    @Override
    @TenantIgnore
    public String execute(String param) {
        Integer count = jobLogService.cleanJobLog(JOB_CLEAN_RETAIN_DAY, DELETE_LIMIT);
        log.info("[execute][定when clean 定when job log count ({})]", count);
        return String.format("定when clean 定when job log count %s", count);
    }

}
