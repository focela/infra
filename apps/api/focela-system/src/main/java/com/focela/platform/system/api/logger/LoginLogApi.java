package com.focela.platform.system.api.logger;

import com.focela.platform.system.api.logger.dto.LoginLogCreateRpcRequest;

import jakarta.validation.Valid;

/**
 * Login log API interface
 */
public interface LoginLogApi {

    /**
     * Create a login log
     *
     * @param request log information
     */
    void createLoginLog(@Valid LoginLogCreateRpcRequest request);

}
