package com.focela.platform.framework.common.contract.infra.logger;

import com.focela.platform.framework.common.contract.infra.logger.dto.ApiErrorLogCreateRpcRequest;

import jakarta.validation.Valid;
import org.springframework.scheduling.annotation.Async;

/**
 * API error log API interface.
 */
public interface ApiErrorLogContractApi {

    /**
     * Create an API error log.
     *
     * @param createDTO create information
     */
    void createApiErrorLog(@Valid ApiErrorLogCreateRpcRequest createDTO);

    /**
     * [Async] Create an API error log.
     *
     * @param createDTO error log DTO
     */
    @Async
    default void createApiErrorLogAsync(ApiErrorLogCreateRpcRequest createDTO) {
        createApiErrorLog(createDTO);
    }

}
