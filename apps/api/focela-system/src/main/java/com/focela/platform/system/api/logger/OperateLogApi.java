package com.focela.platform.system.api.logger;

import com.focela.platform.common.api.system.logger.OperateLogContractApi;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.system.api.logger.dto.OperateLogPageRpcRequest;
import com.focela.platform.system.api.logger.dto.OperateLogRpcResponse;

/**
 * Operate log API interface
 */
public interface OperateLogApi extends OperateLogContractApi {

    /**
     * Get the operate log page for the specified data of the specified module
     *
     * @param pageRequest request
     * @return operate log page
     */
    PageResult<OperateLogRpcResponse> getOperateLogPage(OperateLogPageRpcRequest pageRequest);

}
