package com.focela.platform.infra.job.logger;

import com.focela.platform.quartz.core.handler.JobHandler;
import com.focela.platform.tenant.core.aop.TenantIgnore;
import com.focela.platform.infra.service.logger.ApiErrorLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Job that physically deletes error logs older than N days.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ErrorLogCleanJob implements JobHandler {

    private final ApiErrorLogService apiErrorLogService;

    /**
     * Clean logs older than (14) days.
     */
    private static final Integer ERROR_LOG_RETENTION_DAYS = 14;

    /**
     * Number of records deleted per batch. Setting this too high may overload the database.
     */
    private static final Integer DELETE_BATCH_SIZE = 100;

    @Override
    @TenantIgnore
    public String execute(String param) {
        Integer count = apiErrorLogService.cleanErrorLog(ERROR_LOG_RETENTION_DAYS, DELETE_BATCH_SIZE);
        log.info("[execute][scheduled clean of error log count ({})]", count);
        return String.format("scheduled clean of error log count %s", count);
    }

}
