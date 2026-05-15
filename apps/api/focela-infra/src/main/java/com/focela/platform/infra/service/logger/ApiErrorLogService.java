package com.focela.platform.infra.service.logger;

import com.focela.platform.common.api.infra.logger.dto.ApiErrorLogCreateRpcRequest;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.infra.controller.admin.logger.dto.apierrorlog.ApiErrorLogPageRequest;
import com.focela.platform.infra.entity.logger.ApiErrorLogEntity;

/**
 * API error log Service interface
 */
public interface ApiErrorLogService {

    /**
     * Create an API error log.
     *
     * @param createRequest API error log
     */
    void createApiErrorLog(ApiErrorLogCreateRpcRequest createRequest);

    /**
     * Get an API error log.
     *
     * @param id ID
     * @return API error log
     */
    ApiErrorLogEntity getApiErrorLog(Long id);

    /**
     * Get a paged list of API error logs.
     *
     * @param pageRequest paged query
     * @return paged API error logs
     */
    PageResult<ApiErrorLogEntity> getApiErrorLogPage(ApiErrorLogPageRequest pageRequest);

    /**
     * Mark an API error log as processed.
     *
     * @param id            API log ID
     * @param processStatus processing result
     * @param processUserId processor user ID
     */
    void updateApiErrorLogProcess(Long id, Integer processStatus, Long processUserId);

    /**
     * Clean error logs older than exceedDay days.
     *
     * @param exceedDay   delete logs older than this many days
     * @param deleteLimit number of records to delete per batch
     */
    Integer cleanErrorLog(Integer exceedDay, Integer deleteLimit);

}
