package com.focela.platform.infra.service.logger;

import com.focela.platform.common.api.infra.logger.dto.ApiAccessLogCreateRpcRequest;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.infra.controller.admin.logger.dto.apiaccesslog.ApiAccessLogPageRequest;
import com.focela.platform.infra.entity.logger.ApiAccessLogEntity;

/**
 * API access log Service interface
 */
public interface ApiAccessLogService {

    /**
     * Create an API access log.
     *
     * @param createRequest API access log
     */
    void createApiAccessLog(ApiAccessLogCreateRpcRequest createRequest);

    /**
     * Get an API access log.
     *
     * @param id ID
     * @return API access log
     */
    ApiAccessLogEntity getApiAccessLog(Long id);

    /**
     * Get a paged list of API access logs.
     *
     * @param pageRequest paged query
     * @return paged API access logs
     */
    PageResult<ApiAccessLogEntity> getApiAccessLogPage(ApiAccessLogPageRequest pageRequest);

    /**
     * Clean access logs older than exceedDay days.
     *
     * @param exceedDay   delete logs older than this many days
     * @param deleteLimit number of records to delete per batch
     */
    Integer cleanAccessLog(Integer exceedDay, Integer deleteLimit);

}
