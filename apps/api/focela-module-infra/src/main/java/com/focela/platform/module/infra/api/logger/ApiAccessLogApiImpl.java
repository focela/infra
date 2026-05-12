package com.focela.platform.module.infra.api.logger;

import com.focela.platform.framework.common.business.infra.logger.ApiAccessLogCommonApi;
import com.focela.platform.framework.common.business.infra.logger.dto.ApiAccessLogCreateReqDTO;
import com.focela.platform.module.infra.service.logger.ApiAccessLogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * API 访问日志的 API 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class ApiAccessLogApiImpl implements ApiAccessLogCommonApi {

    @Resource
    private ApiAccessLogService apiAccessLogService;

    @Override
    public void createApiAccessLog(ApiAccessLogCreateReqDTO createDTO) {
        apiAccessLogService.createApiAccessLog(createDTO);
    }

}
