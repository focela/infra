package com.focela.platform.module.system.service.logger;

import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.framework.common.business.system.logger.dto.OperateLogCreateReqDTO;
import com.focela.platform.module.system.api.logger.dto.OperateLogPageReqDTO;
import com.focela.platform.module.system.controller.admin.logger.dto.operatelog.OperateLogPageRequest;
import com.focela.platform.module.system.entity.logger.OperateLogEntity;
import com.focela.platform.module.system.repository.mapper.logger.OperateLogMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * 操作日志 Service 实现类
 */
@Service
@Validated
@Slf4j
public class DefaultOperateLogService implements OperateLogService {

    @Resource
    private OperateLogMapper operateLogMapper;

    @Override
    public void createOperateLog(OperateLogCreateReqDTO createReqDTO) {
        OperateLogEntity log = BeanUtils.toBean(createReqDTO, OperateLogEntity.class);
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
    public PageResult<OperateLogEntity> getOperateLogPage(OperateLogPageReqDTO pageReqDTO) {
        return operateLogMapper.selectPage(pageReqDTO);
    }

}
