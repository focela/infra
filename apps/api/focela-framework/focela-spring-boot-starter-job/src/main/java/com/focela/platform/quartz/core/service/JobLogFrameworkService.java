package com.focela.platform.quartz.core.service;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Job log framework service interface.
 */
public interface JobLogFrameworkService {

    /**
     * Create a Job log.
     *
     * @param jobId           task ID
     * @param beginTime       start time
     * @param jobHandlerName  job handler name
     * @param jobHandlerParam job handler parameter
     * @param executeIndex    execution attempt number
     * @return Job log ID
     */
    Long createJobLog(@NotNull(message = "task ID must not be blank") Long jobId,
                      @NotNull(message = "start time must not be blank") LocalDateTime beginTime,
                      @NotEmpty(message = "job handler name must not be blank") String jobHandlerName,
                      String jobHandlerParam,
                      @NotNull(message = "execution attempt number must not be blank") Integer executeIndex);

    /**
     * Update the execution result of a Job log.
     *
     * @param logId    log ID
     * @param endTime  end time. Recorded asynchronously to avoid inaccurate timing.
     * @param duration run duration in milliseconds
     * @param success  whether execution succeeded
     * @param result   result data
     */
    void updateJobLogResultAsync(@NotNull(message = "log ID must not be blank") Long logId,
                                 @NotNull(message = "end time must not be blank") LocalDateTime endTime,
                                 @NotNull(message = "run duration must not be blank") Integer duration,
                                 boolean success, String result);
}
