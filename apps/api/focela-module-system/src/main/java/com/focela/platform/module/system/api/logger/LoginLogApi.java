package com.focela.platform.module.system.api.logger;

import com.focela.platform.module.system.api.logger.dto.LoginLogCreateRpcRequest;

import jakarta.validation.Valid;

/**
 * 登录日志的 API 接口
 */
public interface LoginLogApi {

    /**
     * 创建登录日志
     *
     * @param reqDTO 日志信息
     */
    void createLoginLog(@Valid LoginLogCreateRpcRequest reqDTO);

}
