package com.focela.platform.quartz.core.handler;

/**
 * Job handler.
 */
public interface JobHandler {

    /**
     * Execute the job.
     *
     * @param param parameter
     * @return result
     * @throws Exception exception
     */
    String execute(String param) throws Exception;

}
