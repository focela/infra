package com.focela.platform.infra.service.job;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.infra.controller.admin.job.dto.JobPageRequest;
import com.focela.platform.infra.controller.admin.job.dto.JobSaveRequest;
import com.focela.platform.infra.entity.job.JobEntity;
import jakarta.validation.Valid;
import org.quartz.SchedulerException;

import java.util.List;

/**
 * Scheduled job Service interface
 */
public interface JobService {

    /**
     * Create a scheduled job.
     *
     * @param createRequest creation info
     * @return ID
     */
    Long createJob(@Valid JobSaveRequest createRequest) throws SchedulerException;

    /**
     * Update a scheduled job.
     *
     * @param updateRequest update info
     */
    void updateJob(@Valid JobSaveRequest updateRequest) throws SchedulerException;

    /**
     * Update the status of a scheduled job.
     *
     * @param id     job ID
     * @param status status
     */
    void updateJobStatus(Long id, Integer status) throws SchedulerException;

    /**
     * Trigger a scheduled job.
     *
     * @param id job ID
     */
    void triggerJob(Long id) throws SchedulerException;

    /**
     * Sync scheduled jobs.
     *
     * Purpose: force-sync the locally stored Job info into Quartz.
     */
    void syncJob() throws SchedulerException;

    /**
     * Delete a scheduled job.
     *
     * @param id ID
     */
    void deleteJob(Long id) throws SchedulerException;

    /**
     * Batch delete scheduled jobs.
     *
     * @param ids ID list
     */
    void deleteJobList(List<Long> ids) throws SchedulerException;

    /**
     * Get a scheduled job.
     *
     * @param id ID
     * @return scheduled job
     */
    JobEntity getJob(Long id);

    /**
     * Get a paged list of scheduled jobs.
     *
     * @param pageRequest paged query
     * @return paged scheduled jobs
     */
    PageResult<JobEntity> getJobPage(JobPageRequest pageRequest);

}
