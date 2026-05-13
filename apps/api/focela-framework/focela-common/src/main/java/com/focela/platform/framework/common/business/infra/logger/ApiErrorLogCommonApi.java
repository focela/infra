package com.focela.platform.framework.common.business.infra.logger;

import com.focela.platform.framework.common.business.infra.logger.dto.ApiErrorLogCreateRpcRequest;

import jakarta.validation.Valid;
import org.springframework.scheduling.annotation.Async;

/**
 * API 错误日志的 API 接口
 */
public interface ApiErrorLogCommonApi {

    /**
     * 创建 API 错误日志
     *
     * @param createDTO 创建信息
     */
    void createApiErrorLog(@Valid ApiErrorLogCreateRpcRequest createDTO);

    /**
     * 【异步】创建 API 异常日志
     *
     * @param createDTO 异常日志 DTO
     */
    @Async
    default void createApiErrorLogAsync(ApiErrorLogCreateRpcRequest createDTO) {
        createApiErrorLog(createDTO);
    }

}
