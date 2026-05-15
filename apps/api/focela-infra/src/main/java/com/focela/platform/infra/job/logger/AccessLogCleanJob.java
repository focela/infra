package com.focela.platform.infra.job.logger;

import com.focela.platform.quartz.core.handler.JobHandler;
import com.focela.platform.tenant.core.aop.TenantIgnore;
import com.focela.platform.infra.service.logger.ApiAccessLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * Job that physically deletes access logs older than N days.
 */
@Component
@Slf4j
public class AccessLogCleanJob implements JobHandler {

    @Resource
    private ApiAccessLogService apiAccessLogService;

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
        Integer count = apiAccessLogService.cleanAccessLog(JOB_CLEAN_RETAIN_DAY, DELETE_LIMIT);
        log.info("[execute][scheduled clean of access log count ({})]", count);
        return String.format("scheduled clean of access log count %s", count);
    }

}
