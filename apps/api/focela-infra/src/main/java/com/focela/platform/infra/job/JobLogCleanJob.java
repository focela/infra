package com.focela.platform.infra.job;

import com.focela.platform.quartz.core.handler.JobHandler;
import com.focela.platform.tenant.core.aop.TenantIgnore;
import com.focela.platform.infra.service.job.JobLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Job that physically deletes job logs older than N days.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JobLogCleanJob implements JobHandler {

    private final JobLogService jobLogService;

    /**
     * Clean logs older than (14) days.
     */
    private static final Integer JOB_CLEAN_RETAIN_DAY = 14;

    /**
     * Number of records deleted per batch. Setting this too high may overload the database.
     */
    private static final Integer DELETE_LIMIT = 100;

    @Override
    @TenantIgnore
    public String execute(String param) {
        Integer count = jobLogService.cleanJobLog(JOB_CLEAN_RETAIN_DAY, DELETE_LIMIT);
        log.info("[execute][scheduled clean of job log count ({})]", count);
        return String.format("scheduled clean of job log count %s", count);
    }

}
