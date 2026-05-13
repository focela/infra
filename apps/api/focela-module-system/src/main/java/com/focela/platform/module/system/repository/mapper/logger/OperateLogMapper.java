package com.focela.platform.module.system.repository.mapper.logger;

import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.system.api.logger.dto.OperateLogPageReqDTO;
import com.focela.platform.module.system.controller.admin.logger.dto.operatelog.OperateLogPageRequest;
import com.focela.platform.module.system.repository.entity.logger.OperateLogEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperateLogMapper extends BaseMapperX<OperateLogEntity> {

    default PageResult<OperateLogEntity> selectPage(OperateLogPageRequest pageReqDTO) {
        return selectPage(pageReqDTO, new LambdaQueryWrapperX<OperateLogEntity>()
                .eqIfPresent(OperateLogEntity::getUserId, pageReqDTO.getUserId())
                .eqIfPresent(OperateLogEntity::getBizId, pageReqDTO.getBizId())
                .likeIfPresent(OperateLogEntity::getType, pageReqDTO.getType())
                .likeIfPresent(OperateLogEntity::getSubType, pageReqDTO.getSubType())
                .likeIfPresent(OperateLogEntity::getAction, pageReqDTO.getAction())
                .betweenIfPresent(OperateLogEntity::getCreateTime, pageReqDTO.getCreateTime())
                .orderByDesc(OperateLogEntity::getId));
    }

    default PageResult<OperateLogEntity> selectPage(OperateLogPageReqDTO pageReqDTO) {
        return selectPage(pageReqDTO, new LambdaQueryWrapperX<OperateLogEntity>()
                .eqIfPresent(OperateLogEntity::getType, pageReqDTO.getType())
                .eqIfPresent(OperateLogEntity::getBizId, pageReqDTO.getBizId())
                .eqIfPresent(OperateLogEntity::getUserId, pageReqDTO.getUserId())
                .orderByDesc(OperateLogEntity::getId));
    }

}
