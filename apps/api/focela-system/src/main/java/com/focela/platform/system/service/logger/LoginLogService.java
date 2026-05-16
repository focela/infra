package com.focela.platform.system.service.logger;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.system.api.logger.dto.LoginLogCreateRpcRequest;
import com.focela.platform.system.controller.admin.logger.dto.loginlog.LoginLogPageRequest;
import com.focela.platform.system.entity.logger.LoginLogEntity;

import jakarta.validation.Valid;

/**
 * Login log Service interface
 */
public interface LoginLogService {

    /**
     * Get login log
     *
     * @param id ID
     * @return login log
     */
    LoginLogEntity getLoginLog(Long id);

    /**
     * Get login log page
     *
     * @param pageRequest page query
     * @return login log page
     */
    PageResult<LoginLogEntity> getLoginLogPage(LoginLogPageRequest pageRequest);

    /**
     * Create login log
     *
     * @param request log info
     */
    void createLoginLog(@Valid LoginLogCreateRpcRequest request);

}
