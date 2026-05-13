package com.focela.platform.module.system.api.logger;

import com.focela.platform.framework.common.business.system.logger.OperateLogCommonApi;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.module.system.api.logger.dto.OperateLogPageRpcRequest;
import com.focela.platform.module.system.api.logger.dto.OperateLogRpcResponse;

/**
 * 操作日志 API 接口
 */
public interface OperateLogApi extends OperateLogCommonApi {

    /**
     * 获取指定模块的指定数据的操作日志分页
     *
     * @param pageReqDTO 请求
     * @return 操作日志分页
     */
    PageResult<OperateLogRpcResponse> getOperateLogPage(OperateLogPageRpcRequest pageReqDTO);

}
