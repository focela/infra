package com.focela.platform.system.service.logger;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.common.api.system.logger.dto.OperateLogCreateRpcRequest;
import com.focela.platform.system.api.logger.dto.OperateLogPageRpcRequest;
import com.focela.platform.system.controller.admin.logger.dto.operatelog.OperateLogPageRequest;
import com.focela.platform.system.entity.logger.OperateLogEntity;
import com.focela.platform.system.repository.mapper.logger.OperateLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Operate log Service implementation class
 */
@Service
@Validated
@Slf4j
@RequiredArgsConstructor
public class DefaultOperateLogService implements OperateLogService {

    private final OperateLogMapper operateLogMapper;

    @Override
    public void createOperateLog(OperateLogCreateRpcRequest createRequest) {
        OperateLogEntity log = BeanUtils.toBean(createRequest, OperateLogEntity.class);
        operateLogMapper.insert(log);
    }

    @Override
    public OperateLogEntity getOperateLog(Long id) {
        return operateLogMapper.selectById(id);
    }

    @Override
    public PageResult<OperateLogEntity> getOperateLogPage(OperateLogPageRequest pageRequest) {
        return operateLogMapper.selectPage(pageRequest);
    }

    @Override
    public PageResult<OperateLogEntity> getOperateLogPage(OperateLogPageRpcRequest pageRequest) {
        return operateLogMapper.selectPage(pageRequest);
    }

}
