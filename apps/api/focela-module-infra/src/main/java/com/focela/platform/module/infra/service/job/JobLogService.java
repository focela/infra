package com.focela.platform.module.infra.service.job;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.quartz.core.service.JobLogFrameworkService;
import com.focela.platform.module.infra.controller.admin.job.vo.log.JobLogPageReqVO;
import com.focela.platform.module.infra.repository.entity.job.JobLogEntity;

/**
 * Job 日志 Service 接口
 *
 * @author 芋道源码
 */
public interface JobLogService extends JobLogFrameworkService {

    /**
     * 获得定时任务
     *
     * @param id 编号
     * @return 定时任务
     */
    JobLogEntity getJobLog(Long id);

    /**
     * 获得定时任务分页
     *
     * @param pageReqVO 分页查询
     * @return 定时任务分页
     */
    PageResult<JobLogEntity> getJobLogPage(JobLogPageReqVO pageReqVO);

    /**
     * 清理 exceedDay 天前的任务日志
     *
     * @param exceedDay   超过多少天就进行清理
     * @param deleteLimit 清理的间隔条数
     */
    Integer cleanJobLog(Integer exceedDay, Integer deleteLimit);

}
