package com.focela.platform.module.system.api.logger;

import com.focela.platform.module.system.api.logger.dto.LoginLogCreateRpcRequest;

import jakarta.validation.Valid;

/**
 * Login log API interface
 */
public interface LoginLogApi {

    /**
     * Create a login log
     *
     * @param reqDTO log information
     */
    void createLoginLog(@Valid LoginLogCreateRpcRequest reqDTO);

}
