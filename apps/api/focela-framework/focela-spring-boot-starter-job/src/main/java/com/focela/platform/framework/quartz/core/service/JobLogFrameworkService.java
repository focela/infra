package com.focela.platform.framework.quartz.core.service;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Job 日志 Framework Service 接口
 */
public interface JobLogFrameworkService {

    /**
     * 创建 Job 日志
     *
     * @param jobId           任务编号
     * @param beginTime       开始时间
     * @param jobHandlerName  Job 处理器的名字
     * @param jobHandlerParam Job 处理器的参数
     * @param executeIndex    第几次执行
     * @return Job 日志的编号
     */
    Long createJobLog(@NotNull(message = "任务ID must not be blank") Long jobId,
                      @NotNull(message = "start time") LocalDateTime beginTime,
                      @NotEmpty(message = "Job handler 名字must not be blank") String jobHandlerName,
                      String jobHandlerParam,
                      @NotNull(message = "第几 times execute must not be blank") Integer executeIndex);

    /**
     * 更新 Job 日志的执行结果
     *
     * @param logId    日志编号
     * @param endTime  结束时间。因为是异步，避免记录时间不准去
     * @param duration 运行时长，单位：毫秒
     * @param success  是否成功
     * @param result   成功数据
     */
    void updateJobLogResultAsync(@NotNull(message = "log ID must not be blank") Long logId,
                                 @NotNull(message = "end time must not be blank") LocalDateTime endTime,
                                 @NotNull(message = "运line when 长must not be blank") Integer duration,
                                 boolean success, String result);
}
