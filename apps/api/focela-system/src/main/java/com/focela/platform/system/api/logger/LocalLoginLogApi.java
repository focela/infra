package com.focela.platform.system.api.logger;

import com.focela.platform.system.api.logger.dto.LoginLogCreateRpcRequest;
import com.focela.platform.system.service.logger.LoginLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Login log API implementation class
 */
@Service
@Validated
@RequiredArgsConstructor
public class LocalLoginLogApi implements LoginLogApi {

    private final LoginLogService loginLogService;

    @Override
    public void createLoginLog(LoginLogCreateRpcRequest request) {
        loginLogService.createLoginLog(request);
    }

}
