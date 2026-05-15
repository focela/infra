package com.focela.platform.infra.api.logger;

import com.focela.platform.common.api.infra.logger.ApiErrorLogContractApi;
import com.focela.platform.common.api.infra.logger.dto.ApiErrorLogCreateRpcRequest;
import com.focela.platform.infra.service.logger.ApiErrorLogService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;

/**
 * Implementation class of the API error log API
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
