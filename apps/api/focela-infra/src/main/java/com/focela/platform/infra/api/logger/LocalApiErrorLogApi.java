package com.focela.platform.infra.api.logger;

import com.focela.platform.common.api.infra.logger.ApiErrorLogContractApi;
import com.focela.platform.common.api.infra.logger.dto.ApiErrorLogCreateRpcRequest;
import com.focela.platform.infra.service.logger.ApiErrorLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Implementation class of the API error log API
 */
@Service
@Validated
@RequiredArgsConstructor
public class LocalApiErrorLogApi implements ApiErrorLogContractApi {

    private final ApiErrorLogService apiErrorLogService;

    @Override
    public void createApiErrorLog(ApiErrorLogCreateRpcRequest createDTO) {
        apiErrorLogService.createApiErrorLog(createDTO);
    }

}
