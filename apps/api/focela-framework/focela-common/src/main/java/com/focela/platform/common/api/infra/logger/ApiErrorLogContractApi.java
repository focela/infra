package com.focela.platform.common.api.infra.logger;

import com.focela.platform.common.api.infra.logger.dto.ApiErrorLogCreateRpcRequest;

import jakarta.validation.Valid;
import org.springframework.scheduling.annotation.Async;

/**
 * API error log API interface.
 */
public interface ApiErrorLogContractApi {

    /**
     * Create an API error log.
     *
     * @param createRequest create information
     */
    void createApiErrorLog(@Valid ApiErrorLogCreateRpcRequest createRequest);

    /**
     * [Async] Create an API error log.
     *
     * @param createRequest error log request
     */
    @Async
    default void createApiErrorLogAsync(ApiErrorLogCreateRpcRequest createRequest) {
        createApiErrorLog(createRequest);
    }

}
