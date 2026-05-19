package com.focela.platform.system.service.logger;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.api.system.logger.dto.OperateLogCreateRpcRequest;
import com.focela.platform.system.api.logger.dto.OperateLogPageRpcRequest;
import com.focela.platform.system.controller.admin.logger.dto.operatelog.OperateLogPageRequest;
import com.focela.platform.system.domain.entity.logger.OperateLogEntity;

/**
 * Operate log Service interface
 */
public interface OperateLogService {

    /**
     * Record an operate log
     *
     * @param createRequest create request
     */
    void createOperateLog(OperateLogCreateRpcRequest createRequest);

    /**
     * Get operate log
     *
     * @param id ID
     * @return operate log
     */
    OperateLogEntity getOperateLog(Long id);

    /**
     * Get operate log page
     *
     * @param pageRequest page query
     * @return operate log page
     */
    PageResult<OperateLogEntity> getOperateLogPage(OperateLogPageRequest pageRequest);

    /**
     * Get operate log page
     *
     * @param pageRequest page query
     * @return operate log page
     */
    PageResult<OperateLogEntity> getOperateLogPage(OperateLogPageRpcRequest pageRequest);

}
