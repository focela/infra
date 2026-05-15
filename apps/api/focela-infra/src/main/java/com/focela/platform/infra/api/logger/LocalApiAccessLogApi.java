package com.focela.platform.infra.api.logger;

import com.focela.platform.common.api.infra.logger.ApiAccessLogContractApi;
import com.focela.platform.common.api.infra.logger.dto.ApiAccessLogCreateRpcRequest;
import com.focela.platform.infra.service.logger.ApiAccessLogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Implementation class of the API access log API
 */
@Service
@Validated
public class LocalApiAccessLogApi implements ApiAccessLogContractApi {

    @Resource
    private ApiAccessLogService apiAccessLogService;

    @Override
    public void createApiAccessLog(ApiAccessLogCreateRpcRequest createDTO) {
        apiAccessLogService.createApiAccessLog(createDTO);
    }

}
