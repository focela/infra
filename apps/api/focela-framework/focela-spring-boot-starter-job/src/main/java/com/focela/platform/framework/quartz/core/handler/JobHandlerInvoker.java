package com.focela.platform.framework.quartz.core.handler;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.thread.ThreadUtil;
import com.focela.platform.framework.quartz.core.enums.JobDataKeyEnum;
import com.focela.platform.framework.quartz.core.service.JobLogFrameworkService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;

import static cn.hutool.core.exceptions.ExceptionUtil.getRootCauseMessage;

/**
 * Base Job invoker responsible for calling {@link JobHandler#execute(String)} to execute the job.
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
@Slf4j
public class JobHandlerInvoker extends QuartzJobBean {

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private JobLogFrameworkService jobLogFrameworkService;

    @Override
    protected void executeInternal(JobExecutionContext executionContext) throws JobExecutionException {
        // Step 1: get the job data
        Long jobId = executionContext.getMergedJobDataMap().getLong(JobDataKeyEnum.JOB_ID.name());
        String jobHandlerName = executionContext.getMergedJobDataMap().getString(JobDataKeyEnum.JOB_HANDLER_NAME.name());
        String jobHandlerParam = executionContext.getMergedJobDataMap().getString(JobDataKeyEnum.JOB_HANDLER_PARAM.name());
        int refireCount  = executionContext.getRefireCount();
        int retryCount = (Integer) executionContext.getMergedJobDataMap().getOrDefault(JobDataKeyEnum.JOB_RETRY_COUNT.name(), 0);
        int retryInterval = (Integer) executionContext.getMergedJobDataMap().getOrDefault(JobDataKeyEnum.JOB_RETRY_INTERVAL.name(), 0);

        // Step 2: execute the job
        Long jobLogId = null;
        LocalDateTime startTime = LocalDateTime.now();
        String data = null;
        Throwable exception = null;
        try {
            // Record the job log (initial)
            jobLogId = jobLogFrameworkService.createJobLog(jobId, startTime, jobHandlerName, jobHandlerParam, refireCount + 1);
            // Execute the job
            data = this.executeInternal(jobHandlerName, jobHandlerParam);
        } catch (Throwable ex) {
            exception = ex;
        }

        // Step 3: record the execution log
        this.updateJobLogResultAsync(jobLogId, startTime, data, exception, executionContext);

        // Step 4: handle the exception case
        handleException(exception, refireCount, retryCount, retryInterval);
    }

    private String executeInternal(String jobHandlerName, String jobHandlerParam) throws Exception {
        // Get the JobHandler bean
        JobHandler jobHandler = applicationContext.getBean(jobHandlerName, JobHandler.class);
        Assert.notNull(jobHandler, "JobHandler must not be null");
        // Execute the job
        return jobHandler.execute(jobHandlerParam);
    }

    private void updateJobLogResultAsync(Long jobLogId, LocalDateTime startTime, String data, Throwable exception,
                                         JobExecutionContext executionContext) {
        LocalDateTime endTime = LocalDateTime.now();
        // Determine success
        boolean success = exception == null;
        if (!success) {
            data = getRootCauseMessage(exception);
        }
        // Update the log
        try {
            jobLogFrameworkService.updateJobLogResultAsync(jobLogId, endTime, (int) LocalDateTimeUtil.between(startTime, endTime).toMillis(), success, data);
        } catch (Exception ex) {
            log.error("[executeInternal][Job({}) logId({}) failed to record execute log ({}/{})]",
                    executionContext.getJobDetail().getKey(), jobLogId, success, data);
        }
    }

    private void handleException(Throwable exception,
                                 int refireCount, int retryCount, int retryInterval) throws JobExecutionException {
        // If there is an exception, retry
        if (exception == null) {
            return;
        }
        // Case 1: retry limit reached - simply rethrow the exception
        if (refireCount >= retryCount) {
            throw new JobExecutionException(exception);
        }

        // Case 2: retry limit not reached - sleep for the interval, then retry
        // sleep is used here to keep the implementation simple; at any given moment we don't expect a large number of failed jobs.
        if (retryInterval > 0) {
            ThreadUtil.sleep(retryInterval);
        }
        // Second argument refireImmediately = true means retry immediately
        throw new JobExecutionException(exception, true);
    }

}
