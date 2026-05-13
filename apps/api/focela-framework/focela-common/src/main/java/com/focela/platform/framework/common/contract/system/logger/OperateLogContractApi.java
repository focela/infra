package com.focela.platform.framework.common.contract.system.logger;

import com.focela.platform.framework.common.contract.system.logger.dto.OperateLogCreateRpcRequest;
import jakarta.validation.Valid;
import org.springframework.scheduling.annotation.Async;

/**
 * 操作日志 API 接口
 */
public interface OperateLogContractApi {

    /**
     * 创建操作日志
     *
     * @param createReqDTO 请求
     */
    void createOperateLog(@Valid OperateLogCreateRpcRequest createReqDTO);

    /**
     * 【异步】创建操作日志
     *
     * @param createReqDTO 请求
     */
    @Async
    default void createOperateLogAsync(OperateLogCreateRpcRequest createReqDTO) {
        createOperateLog(createReqDTO);
    }

}
