package com.focela.platform.quartz.core.scheduler;

import com.focela.platform.quartz.core.enums.JobDataKeyEnum;
import com.focela.platform.quartz.core.handler.JobHandlerInvoker;
import org.quartz.*;

import static com.focela.platform.common.exception.enums.GlobalErrorCodeConstants.NOT_IMPLEMENTED;
import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception0;

/**
 * Manager for {@link org.quartz.Scheduler}, responsible for creating jobs.
 *
 * For implementation simplicity, jobHandlerName is used as the unique identifier:
 * 1. Job's {@link JobDetail#getKey()}
 * 2. Trigger's {@link Trigger#getKey()}
 *
 * In addition, jobHandlerName matches the Spring bean name and is invoked directly.
 */
public class SchedulerManager {

    private final Scheduler scheduler;

    public SchedulerManager(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * Add a Job to Quartz.
     *
     * @param jobId task ID
     * @param jobHandlerName job handler name
     * @param jobHandlerParam job handler parameter
     * @param cronExpression CRON expression
     * @param retryCount retry count
     * @param retryInterval retry interval
     * @throws SchedulerException scheduler exception when adding
     */
    public void addJob(Long jobId, String jobHandlerName, String jobHandlerParam, String cronExpression,
                       Integer retryCount, Integer retryInterval)
            throws SchedulerException {
        validateScheduler();
        // Create the JobDetail
        JobDetail jobDetail = JobBuilder.newJob(JobHandlerInvoker.class)
                .usingJobData(JobDataKeyEnum.JOB_ID.name(), jobId)
                .usingJobData(JobDataKeyEnum.JOB_HANDLER_NAME.name(), jobHandlerName)
                .withIdentity(jobHandlerName).build();
        // Create the Trigger
        Trigger trigger = this.buildTrigger(jobHandlerName, jobHandlerParam, cronExpression, retryCount, retryInterval);
        // Schedule the new Job
        scheduler.scheduleJob(jobDetail, trigger);
    }

    /**
     * Update a Job in Quartz.
     *
     * @param jobHandlerName job handler name
     * @param jobHandlerParam job handler parameter
     * @param cronExpression CRON expression
     * @param retryCount retry count
     * @param retryInterval retry interval
     * @throws SchedulerException scheduler exception when updating
     */
    public void updateJob(String jobHandlerName, String jobHandlerParam, String cronExpression,
                          Integer retryCount, Integer retryInterval)
            throws SchedulerException {
        validateScheduler();
        // Create the new Trigger
        Trigger newTrigger = this.buildTrigger(jobHandlerName, jobHandlerParam, cronExpression, retryCount, retryInterval);
        // Reschedule
        scheduler.rescheduleJob(new TriggerKey(jobHandlerName), newTrigger);
    }

    /**
     * Delete a Job from Quartz.
     *
     * @param jobHandlerName job handler name
     * @throws SchedulerException scheduler exception when deleting
     */
    public void deleteJob(String jobHandlerName) throws SchedulerException {
        validateScheduler();
        // Pause the Trigger
        scheduler.pauseTrigger(new TriggerKey(jobHandlerName));
        // Unschedule and delete the Job
        scheduler.unscheduleJob(new TriggerKey(jobHandlerName));
        scheduler.deleteJob(new JobKey(jobHandlerName));
    }

    /**
     * Pause a Job in Quartz.
     *
     * @param jobHandlerName job handler name
     * @throws SchedulerException scheduler exception when pausing
     */
    public void pauseJob(String jobHandlerName) throws SchedulerException {
        validateScheduler();
        scheduler.pauseJob(new JobKey(jobHandlerName));
    }

    /**
     * Resume a Job in Quartz.
     *
     * @param jobHandlerName job handler name
     * @throws SchedulerException scheduler exception when resuming
     */
    public void resumeJob(String jobHandlerName) throws SchedulerException {
        validateScheduler();
        scheduler.resumeJob(new JobKey(jobHandlerName));
        scheduler.resumeTrigger(new TriggerKey(jobHandlerName));
    }

    /**
     * Trigger a Job in Quartz immediately, once.
     *
     * @param jobId task ID
     * @param jobHandlerName job handler name
     * @param jobHandlerParam job handler parameter
     * @throws SchedulerException scheduler exception when triggering
     */
    public void triggerJob(Long jobId, String jobHandlerName, String jobHandlerParam)
            throws SchedulerException {
        validateScheduler();
        // Trigger the job
        JobDataMap data = new JobDataMap(); // No retry needed, so retryCount and retryInterval are not set
        data.put(JobDataKeyEnum.JOB_ID.name(), jobId);
        data.put(JobDataKeyEnum.JOB_HANDLER_NAME.name(), jobHandlerName);
        data.put(JobDataKeyEnum.JOB_HANDLER_PARAM.name(), jobHandlerParam);
        scheduler.triggerJob(new JobKey(jobHandlerName), data);
    }

    private Trigger buildTrigger(String jobHandlerName, String jobHandlerParam, String cronExpression,
                                 Integer retryCount, Integer retryInterval) {
        return TriggerBuilder.newTrigger()
                .withIdentity(jobHandlerName)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .usingJobData(JobDataKeyEnum.JOB_HANDLER_PARAM.name(), jobHandlerParam)
                .usingJobData(JobDataKeyEnum.JOB_RETRY_COUNT.name(), retryCount)
                .usingJobData(JobDataKeyEnum.JOB_RETRY_INTERVAL.name(), retryInterval)
                .build();
    }

    private void validateScheduler() {
        if (scheduler == null) {
            throw exception0(NOT_IMPLEMENTED.getCode(),
                    "[scheduled task - disabled][see https://www.example.com/job/ to enable]");
        }
    }

}
