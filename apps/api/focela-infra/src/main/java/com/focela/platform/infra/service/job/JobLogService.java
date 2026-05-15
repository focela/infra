package com.focela.platform.infra.service.job;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.quartz.core.service.JobLogFrameworkService;
import com.focela.platform.infra.controller.admin.job.dto.log.JobLogPageRequest;
import com.focela.platform.infra.entity.job.JobLogEntity;

/**
 * Job log Service interface
 */
public interface JobLogService extends JobLogFrameworkService {

    /**
     * Get a job log.
     *
     * @param id ID
     * @return job log
     */
    JobLogEntity getJobLog(Long id);

    /**
     * Get a paged list of job logs.
     *
     * @param pageRequest paged query
     * @return paged job logs
     */
    PageResult<JobLogEntity> getJobLogPage(JobLogPageRequest pageRequest);

    /**
     * Clean job logs older than exceedDay days.
     *
     * @param exceedDay   delete logs older than this many days
     * @param deleteLimit number of records to delete per batch
     */
    Integer cleanJobLog(Integer exceedDay, Integer deleteLimit);

}
