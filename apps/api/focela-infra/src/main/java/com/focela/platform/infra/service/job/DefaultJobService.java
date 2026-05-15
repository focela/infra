package com.focela.platform.infra.service.job;

import cn.hutool.extra.spring.SpringUtil;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.quartz.core.handler.JobHandler;
import com.focela.platform.quartz.core.scheduler.SchedulerManager;
import com.focela.platform.quartz.core.utils.CronUtils;
import com.focela.platform.infra.controller.admin.job.dto.JobPageRequest;
import com.focela.platform.infra.controller.admin.job.dto.JobSaveRequest;
import com.focela.platform.infra.entity.job.JobEntity;
import com.focela.platform.infra.repository.mapper.job.JobMapper;
import com.focela.platform.infra.enums.job.JobStatusEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.common.utils.collection.CollectionUtils.containsAny;
import static com.focela.platform.infra.constants.ErrorCodeConstants.*;

/**
 * Implementation class of the scheduled job Service
 */
@Service
@Validated
@Slf4j
public class DefaultJobService implements JobService {

    @Resource
    private JobMapper jobMapper;

    @Resource
    private SchedulerManager schedulerManager;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createJob(JobSaveRequest createRequest) throws SchedulerException {
        validateCronExpression(createRequest.getCronExpression());
        // 1.1 Validate uniqueness
        if (jobMapper.selectByHandlerName(createRequest.getHandlerName()) != null) {
            throw exception(JOB_HANDLER_EXISTS);
        }
        // 1.2 Validate that the JobHandler exists
        validateJobHandlerExists(createRequest.getHandlerName());

        // 2. Insert JobEntity
        JobEntity job = BeanUtils.toBean(createRequest, JobEntity.class);
        job.setStatus(JobStatusEnum.INIT.getStatus());
        fillJobMonitorTimeoutEmpty(job);
        jobMapper.insert(job);

        // 3.1 Add the Job to Quartz
        schedulerManager.addJob(job.getId(), job.getHandlerName(), job.getHandlerParam(), job.getCronExpression(),
                createRequest.getRetryCount(), createRequest.getRetryInterval());
        // 3.2 Update JobEntity
        JobEntity updateObj = JobEntity.builder().id(job.getId()).status(JobStatusEnum.NORMAL.getStatus()).build();
        jobMapper.updateById(updateObj);
        return job.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateJob(JobSaveRequest updateRequest) throws SchedulerException {
        validateCronExpression(updateRequest.getCronExpression());
        // 1.1 Verify it exists
        JobEntity job = validateJobExists(updateRequest.getId());
        // 1.2 Only allowed when status is NORMAL. Reason: in PAUSED state, updating the Quartz Job would re-start it.
        if (!job.getStatus().equals(JobStatusEnum.NORMAL.getStatus())) {
            throw exception(JOB_UPDATE_ONLY_NORMAL_STATUS);
        }
        // 1.3 Validate that the JobHandler exists
        validateJobHandlerExists(updateRequest.getHandlerName());

        // 2. Update JobEntity
        JobEntity updateObj = BeanUtils.toBean(updateRequest, JobEntity.class);
        fillJobMonitorTimeoutEmpty(updateObj);
        jobMapper.updateById(updateObj);

        // 3. Update the Job in Quartz
        schedulerManager.updateJob(job.getHandlerName(), updateRequest.getHandlerParam(), updateRequest.getCronExpression(),
                updateRequest.getRetryCount(), updateRequest.getRetryInterval());
    }

    private void validateJobHandlerExists(String handlerName) {
        try {
            Object handler = SpringUtil.getBean(handlerName);
            assert handler != null;
            if (!(handler instanceof JobHandler)) {
                throw exception(JOB_HANDLER_BEAN_TYPE_ERROR);
            }
        } catch (NoSuchBeanDefinitionException e) {
            throw exception(JOB_HANDLER_BEAN_NOT_EXISTS);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateJobStatus(Long id, Integer status) throws SchedulerException {
        // Validate status
        if (!containsAny(status, JobStatusEnum.NORMAL.getStatus(), JobStatusEnum.STOP.getStatus())) {
            throw exception(JOB_CHANGE_STATUS_INVALID);
        }
        // Verify it exists
        JobEntity job = validateJobExists(id);
        // Check whether it is already in the target status
        if (job.getStatus().equals(status)) {
            throw exception(JOB_CHANGE_STATUS_EQUALS);
        }
        // Update Job status
        JobEntity updateObj = JobEntity.builder().id(id).status(status).build();
        jobMapper.updateById(updateObj);

        // Update the Job status in Quartz
        if (JobStatusEnum.NORMAL.getStatus().equals(status)) { // Resume
            schedulerManager.resumeJob(job.getHandlerName());
        } else { // Pause
            schedulerManager.pauseJob(job.getHandlerName());
        }
    }

    @Override
    public void triggerJob(Long id) throws SchedulerException {
        // Verify it exists
        JobEntity job = validateJobExists(id);

        // Trigger the Job in Quartz
        schedulerManager.triggerJob(job.getId(), job.getHandlerName(), job.getHandlerParam());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncJob() throws SchedulerException {
        // 1. Query Job configs
        List<JobEntity> jobList = jobMapper.selectList();

        // 2. Process each
        for (JobEntity job : jobList) {
            // 2.1 Delete first, then create
            schedulerManager.deleteJob(job.getHandlerName());
            schedulerManager.addJob(job.getId(), job.getHandlerName(), job.getHandlerParam(), job.getCronExpression(),
                    job.getRetryCount(), job.getRetryInterval());
            // 2.2 If status is STOP, pause
            if (Objects.equals(job.getStatus(), JobStatusEnum.STOP.getStatus())) {
                schedulerManager.pauseJob(job.getHandlerName());
            }
            log.info("[syncJob][id({}) handlerName({}) sync complete]", job.getId(), job.getHandlerName());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJob(Long id) throws SchedulerException {
        // Verify it exists
        JobEntity job = validateJobExists(id);
        // Update
        jobMapper.deleteById(id);

        // Delete Job from Quartz
        schedulerManager.deleteJob(job.getHandlerName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJobList(List<Long> ids) throws SchedulerException {
        // Batch delete
        List<JobEntity> jobs = jobMapper.selectByIds(ids);
        jobMapper.deleteByIds(ids);

        // Delete Jobs from Quartz
        for (JobEntity job : jobs) {
            schedulerManager.deleteJob(job.getHandlerName());
        }
    }

    private JobEntity validateJobExists(Long id) {
        JobEntity job = jobMapper.selectById(id);
        if (job == null) {
            throw exception(JOB_NOT_EXISTS);
        }
        return job;
    }

    private void validateCronExpression(String cronExpression) {
        if (!CronUtils.isValid(cronExpression)) {
            throw exception(JOB_CRON_EXPRESSION_VALID);
        }
    }

    @Override
    public JobEntity getJob(Long id) {
        return jobMapper.selectById(id);
    }

    @Override
    public PageResult<JobEntity> getJobPage(JobPageRequest pageRequest) {
        return jobMapper.selectPage(pageRequest);
    }

    private static void fillJobMonitorTimeoutEmpty(JobEntity job) {
        if (job.getMonitorTimeout() == null) {
            job.setMonitorTimeout(0);
        }
    }

}
