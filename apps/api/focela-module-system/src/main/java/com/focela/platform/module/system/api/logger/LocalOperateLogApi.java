package com.focela.platform.module.system.api.logger;

import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.framework.common.contract.system.logger.dto.OperateLogCreateRpcRequest;
import com.focela.platform.module.system.api.logger.dto.OperateLogPageRpcRequest;
import com.focela.platform.module.system.api.logger.dto.OperateLogRpcResponse;
import com.focela.platform.module.system.entity.logger.OperateLogEntity;
import com.focela.platform.module.system.service.logger.OperateLogService;
import com.fhs.core.trans.anno.TransMethodResult;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * 操作日志 API 实现类
 */
@Service
@Validated
public class LocalOperateLogApi implements OperateLogApi {

    @Resource
    private OperateLogService operateLogService;

    @Override
    public void createOperateLog(OperateLogCreateRpcRequest createReqDTO) {
        operateLogService.createOperateLog(createReqDTO);
    }

    @Override
    @TransMethodResult
    public PageResult<OperateLogRpcResponse> getOperateLogPage(OperateLogPageRpcRequest pageReqDTO) {
        PageResult<OperateLogEntity> operateLogPage = operateLogService.getOperateLogPage(pageReqDTO);
        return BeanUtils.toBean(operateLogPage, OperateLogRpcResponse.class);
    }

}
