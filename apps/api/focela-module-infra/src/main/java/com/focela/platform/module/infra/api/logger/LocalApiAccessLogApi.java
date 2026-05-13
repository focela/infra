package com.focela.platform.module.infra.api.logger;

import com.focela.platform.framework.common.business.infra.logger.ApiAccessLogCommonApi;
import com.focela.platform.framework.common.business.infra.logger.dto.ApiAccessLogCreateRpcRequest;
import com.focela.platform.module.infra.service.logger.ApiAccessLogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * API 访问日志的 API 实现类
 */
@Service
@Validated
public class LocalApiAccessLogApi implements ApiAccessLogCommonApi {

    @Resource
    private ApiAccessLogService apiAccessLogService;

    @Override
    public void createApiAccessLog(ApiAccessLogCreateRpcRequest createDTO) {
        apiAccessLogService.createApiAccessLog(createDTO);
    }

}
