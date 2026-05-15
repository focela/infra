package com.focela.platform.system.api.logger;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.common.api.system.logger.dto.OperateLogCreateRpcRequest;
import com.focela.platform.system.api.logger.dto.OperateLogPageRpcRequest;
import com.focela.platform.system.api.logger.dto.OperateLogRpcResponse;
import com.focela.platform.system.entity.logger.OperateLogEntity;
import com.focela.platform.system.service.logger.OperateLogService;
import com.fhs.core.trans.anno.TransMethodResult;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Operate log API implementation class
 */
@Service
@Validated
public class LocalOperateLogApi implements OperateLogApi {

    @Resource
    private OperateLogService operateLogService;

    @Override
    public void createOperateLog(OperateLogCreateRpcRequest createRequest) {
        operateLogService.createOperateLog(createRequest);
    }

    @Override
    @TransMethodResult
    public PageResult<OperateLogRpcResponse> getOperateLogPage(OperateLogPageRpcRequest pageRequest) {
        PageResult<OperateLogEntity> operateLogPage = operateLogService.getOperateLogPage(pageRequest);
        return BeanUtils.toBean(operateLogPage, OperateLogRpcResponse.class);
    }

}
