package com.focela.platform.infra.service.job;

import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.infra.controller.admin.job.dto.JobPageRequest;
import com.focela.platform.infra.controller.admin.job.dto.JobSaveRequest;
import com.focela.platform.infra.entity.job.JobEntity;
import jakarta.validation.Valid;
import org.quartz.SchedulerException;

import java.util.List;

/**
 * 定时任务 Service 接口
 */
public interface JobService {

    /**
     * 创建定时任务
     *
     * @param createRequest 创建信息
     * @return 编号
     */
    Long createJob(@Valid JobSaveRequest createRequest) throws SchedulerException;

    /**
     * 更新定时任务
     *
     * @param updateRequest 更新信息
     */
    void updateJob(@Valid JobSaveRequest updateRequest) throws SchedulerException;

    /**
     * 更新定时任务的状态
     *
     * @param id     任务编号
     * @param status 状态
     */
    void updateJobStatus(Long id, Integer status) throws SchedulerException;

    /**
     * 触发定时任务
     *
     * @param id 任务编号
     */
    void triggerJob(Long id) throws SchedulerException;

    /**
     * 同步定时任务
     *
     * 目的：自己存储的 Job 信息，强制同步到 Quartz 中
     */
    void syncJob() throws SchedulerException;

    /**
     * 删除定时任务
     *
     * @param id 编号
     */
    void deleteJob(Long id) throws SchedulerException;

    /**
     * 批量删除定时任务
     *
     * @param ids 编号列表
     */
    void deleteJobList(List<Long> ids) throws SchedulerException;

    /**
     * 获得定时任务
     *
     * @param id 编号
     * @return 定时任务
     */
    JobEntity getJob(Long id);

    /**
     * 获得定时任务分页
     *
     * @param pageRequest 分页查询
     * @return 定时任务分页
     */
    PageResult<JobEntity> getJobPage(JobPageRequest pageRequest);

}
