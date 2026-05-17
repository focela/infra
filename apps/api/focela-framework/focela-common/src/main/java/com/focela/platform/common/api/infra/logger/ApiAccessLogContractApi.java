package com.focela.platform.common.api.infra.logger;

import com.focela.platform.common.api.infra.logger.dto.ApiAccessLogCreateRpcRequest;
import jakarta.validation.Valid;
import org.springframework.scheduling.annotation.Async;

/**
 * API access log API interface.
 */
public interface ApiAccessLogContractApi {

    /**
     * Create an API access log.
     *
     * @param createRequest create information
     */
    void createApiAccessLog(@Valid ApiAccessLogCreateRpcRequest createRequest);

    /**
     * [Async] Create an API access log.
     *
     * @param createRequest access log DTO
     */
    @Async
    default void createApiAccessLogAsync(ApiAccessLogCreateRpcRequest createRequest) {
        createApiAccessLog(createRequest);
    }

}
