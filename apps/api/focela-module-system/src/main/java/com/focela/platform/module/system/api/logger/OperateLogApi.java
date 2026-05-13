package com.focela.platform.module.system.api.logger;

import com.focela.platform.framework.common.contract.system.logger.OperateLogContractApi;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.module.system.api.logger.dto.OperateLogPageRpcRequest;
import com.focela.platform.module.system.api.logger.dto.OperateLogRpcResponse;

/**
 * 操作日志 API 接口
 */
public interface OperateLogApi extends OperateLogContractApi {

    /**
     * 获取指定模块的指定数据的操作日志分页
     *
     * @param pageRequest 请求
     * @return 操作日志分页
     */
    PageResult<OperateLogRpcResponse> getOperateLogPage(OperateLogPageRpcRequest pageRequest);

}
