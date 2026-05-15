package com.focela.platform.framework.common.api.infra.logger;

import com.focela.platform.framework.common.api.infra.logger.dto.ApiAccessLogCreateRpcRequest;
import jakarta.validation.Valid;
import org.springframework.scheduling.annotation.Async;

/**
 * API access log API interface.
 */
public interface ApiAccessLogContractApi {

    /**
     * Create an API access log.
     *
     * @param createDTO create information
     */
    void createApiAccessLog(@Valid ApiAccessLogCreateRpcRequest createDTO);

    /**
     * [Async] Create an API access log.
     *
     * @param createDTO access log DTO
     */
    @Async
    default void createApiAccessLogAsync(ApiAccessLogCreateRpcRequest createDTO) {
        createApiAccessLog(createDTO);
    }

}
