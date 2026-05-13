package com.focela.platform.module.system.repository.mapper.logger;

import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.system.api.logger.dto.OperateLogPageRpcRequest;
import com.focela.platform.module.system.controller.admin.logger.dto.operatelog.OperateLogPageRequest;
import com.focela.platform.module.system.entity.logger.OperateLogEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperateLogMapper extends BaseMapperX<OperateLogEntity> {

    default PageResult<OperateLogEntity> selectPage(OperateLogPageRequest pageRequest) {
        return selectPage(pageRequest, new LambdaQueryWrapperX<OperateLogEntity>()
                .eqIfPresent(OperateLogEntity::getUserId, pageRequest.getUserId())
                .eqIfPresent(OperateLogEntity::getBizId, pageRequest.getBizId())
                .likeIfPresent(OperateLogEntity::getType, pageRequest.getType())
                .likeIfPresent(OperateLogEntity::getSubType, pageRequest.getSubType())
                .likeIfPresent(OperateLogEntity::getAction, pageRequest.getAction())
                .betweenIfPresent(OperateLogEntity::getCreateTime, pageRequest.getCreateTime())
                .orderByDesc(OperateLogEntity::getId));
    }

    default PageResult<OperateLogEntity> selectPage(OperateLogPageRpcRequest pageRequest) {
        return selectPage(pageRequest, new LambdaQueryWrapperX<OperateLogEntity>()
                .eqIfPresent(OperateLogEntity::getType, pageRequest.getType())
                .eqIfPresent(OperateLogEntity::getBizId, pageRequest.getBizId())
                .eqIfPresent(OperateLogEntity::getUserId, pageRequest.getUserId())
                .orderByDesc(OperateLogEntity::getId));
    }

}
