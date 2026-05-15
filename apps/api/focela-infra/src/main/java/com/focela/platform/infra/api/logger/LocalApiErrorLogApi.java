package com.focela.platform.infra.api.logger;

import com.focela.platform.framework.common.contract.infra.logger.ApiErrorLogContractApi;
import com.focela.platform.framework.common.contract.infra.logger.dto.ApiErrorLogCreateRpcRequest;
import com.focela.platform.infra.service.logger.ApiErrorLogService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;

/**
 * API 访问日志的 API 接口
 */
@Service
@Validated
public class LocalApiErrorLogApi implements ApiErrorLogContractApi {

    @Resource
    private ApiErrorLogService apiErrorLogService;

    @Override
    public void createApiErrorLog(ApiErrorLogCreateRpcRequest createDTO) {
        apiErrorLogService.createApiErrorLog(createDTO);
    }

}
