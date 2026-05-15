package com.focela.platform.system.api.logger;

import com.focela.platform.system.api.logger.dto.LoginLogCreateRpcRequest;
import com.focela.platform.system.service.logger.LoginLogService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;

/**
 * Login log API implementation class
 */
@Service
@Validated
public class LocalLoginLogApi implements LoginLogApi {

    @Resource
    private LoginLogService loginLogService;

    @Override
    public void createLoginLog(LoginLogCreateRpcRequest reqDTO) {
        loginLogService.createLoginLog(reqDTO);
    }

}
