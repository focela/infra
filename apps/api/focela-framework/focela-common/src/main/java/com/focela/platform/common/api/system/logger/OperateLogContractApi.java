package com.focela.platform.common.api.system.logger;

import com.focela.platform.common.api.system.logger.dto.OperateLogCreateRpcRequest;
import jakarta.validation.Valid;
import org.springframework.scheduling.annotation.Async;

/**
 * Operate log API interface.
 */
public interface OperateLogContractApi {

    /**
     * Create an operate log.
     *
     * @param createRequest request
     */
    void createOperateLog(@Valid OperateLogCreateRpcRequest createRequest);

    /**
     * [Async] Create an operate log.
     *
     * @param createRequest request
     */
    @Async
    default void createOperateLogAsync(OperateLogCreateRpcRequest createRequest) {
        createOperateLog(createRequest);
    }

}
