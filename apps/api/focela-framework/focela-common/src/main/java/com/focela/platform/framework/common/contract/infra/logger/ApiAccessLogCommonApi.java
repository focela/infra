package com.focela.platform.framework.common.contract.infra.logger;

import com.focela.platform.framework.common.contract.infra.logger.dto.ApiAccessLogCreateRpcRequest;
import jakarta.validation.Valid;
import org.springframework.scheduling.annotation.Async;

/**
 * API 访问日志的 API 接口
 */
public interface ApiAccessLogCommonApi {

    /**
     * 创建 API 访问日志
     *
     * @param createDTO 创建信息
     */
    void createApiAccessLog(@Valid ApiAccessLogCreateRpcRequest createDTO);

    /**
     * 【异步】创建 API 访问日志
     *
     * @param createDTO 访问日志 DTO
     */
    @Async
    default void createApiAccessLogAsync(ApiAccessLogCreateRpcRequest createDTO) {
        createApiAccessLog(createDTO);
    }

}
