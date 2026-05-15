package com.focela.platform.infra.service.job;

import cn.hutool.extra.spring.SpringUtil;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.framework.quartz.core.handler.JobHandler;
import com.focela.platform.framework.quartz.core.scheduler.SchedulerManager;
import com.focela.platform.framework.quartz.core.utils.CronUtils;
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

import static com.focela.platform.framework.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.framework.common.utils.collection.CollectionUtils.containsAny;
import static com.focela.platform.infra.constants.ErrorCodeConstants.*;

/**
 * 定时任务 Service 实现类
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
        // 1.1 校验唯一性
        if (jobMapper.selectByHandlerName(createRequest.getHandlerName()) != null) {
            throw exception(JOB_HANDLER_EXISTS);
        }
        // 1.2 校验 JobHandler 是否存在
        validateJobHandlerExists(createRequest.getHandlerName());

        // 2. 插入 JobEntity
        JobEntity job = BeanUtils.toBean(createRequest, JobEntity.class);
        job.setStatus(JobStatusEnum.INIT.getStatus());
        fillJobMonitorTimeoutEmpty(job);
        jobMapper.insert(job);

        // 3.1 添加 Job 到 Quartz 中
        schedulerManager.addJob(job.getId(), job.getHandlerName(), job.getHandlerParam(), job.getCronExpression(),
                createRequest.getRetryCount(), createRequest.getRetryInterval());
        // 3.2 更新 JobEntity
        JobEntity updateObj = JobEntity.builder().id(job.getId()).status(JobStatusEnum.NORMAL.getStatus()).build();
        jobMapper.updateById(updateObj);
        return job.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateJob(JobSaveRequest updateRequest) throws SchedulerException {
        validateCronExpression(updateRequest.getCronExpression());
        // 1.1 校验存在
        JobEntity job = validateJobExists(updateRequest.getId());
        // 1.2 只有开启状态，才可以修改.原因是，如果出暂停状态，修改 Quartz Job 时，会导致任务又开始执行
        if (!job.getStatus().equals(JobStatusEnum.NORMAL.getStatus())) {
            throw exception(JOB_UPDATE_ONLY_NORMAL_STATUS);
        }
        // 1.3 校验 JobHandler 是否存在
        validateJobHandlerExists(updateRequest.getHandlerName());

        // 2. 更新 JobEntity
        JobEntity updateObj = BeanUtils.toBean(updateRequest, JobEntity.class);
        fillJobMonitorTimeoutEmpty(updateObj);
        jobMapper.updateById(updateObj);

        // 3. 更新 Job 到 Quartz 中
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
        // 校验 status
        if (!containsAny(status, JobStatusEnum.NORMAL.getStatus(), JobStatusEnum.STOP.getStatus())) {
            throw exception(JOB_CHANGE_STATUS_INVALID);
        }
        // 校验存在
        JobEntity job = validateJobExists(id);
        // 校验是否已经为当前状态
        if (job.getStatus().equals(status)) {
            throw exception(JOB_CHANGE_STATUS_EQUALS);
        }
        // 更新 Job 状态
        JobEntity updateObj = JobEntity.builder().id(id).status(status).build();
        jobMapper.updateById(updateObj);

        // 更新状态 Job 到 Quartz 中
        if (JobStatusEnum.NORMAL.getStatus().equals(status)) { // 开启
            schedulerManager.resumeJob(job.getHandlerName());
        } else { // 暂停
            schedulerManager.pauseJob(job.getHandlerName());
        }
    }

    @Override
    public void triggerJob(Long id) throws SchedulerException {
        // 校验存在
        JobEntity job = validateJobExists(id);

        // 触发 Quartz 中的 Job
        schedulerManager.triggerJob(job.getId(), job.getHandlerName(), job.getHandlerParam());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncJob() throws SchedulerException {
        // 1. 查询 Job 配置
        List<JobEntity> jobList = jobMapper.selectList();

        // 2. 遍历处理
        for (JobEntity job : jobList) {
            // 2.1 先删除，再创建
            schedulerManager.deleteJob(job.getHandlerName());
            schedulerManager.addJob(job.getId(), job.getHandlerName(), job.getHandlerParam(), job.getCronExpression(),
                    job.getRetryCount(), job.getRetryInterval());
            // 2.2 如果 status 为暂停，则需要暂停
            if (Objects.equals(job.getStatus(), JobStatusEnum.STOP.getStatus())) {
                schedulerManager.pauseJob(job.getHandlerName());
            }
            log.info("[syncJob][id({}) handlerName({}) sync complete]", job.getId(), job.getHandlerName());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJob(Long id) throws SchedulerException {
        // 校验存在
        JobEntity job = validateJobExists(id);
        // 更新
        jobMapper.deleteById(id);

        // 删除 Job 到 Quartz 中
        schedulerManager.deleteJob(job.getHandlerName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJobList(List<Long> ids) throws SchedulerException {
        // 批量删除
        List<JobEntity> jobs = jobMapper.selectByIds(ids);
        jobMapper.deleteByIds(ids);

        // 删除 Job 到 Quartz 中
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
