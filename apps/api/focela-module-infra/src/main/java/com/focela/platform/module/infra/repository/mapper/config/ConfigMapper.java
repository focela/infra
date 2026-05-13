package com.focela.platform.module.infra.repository.mapper.config;

import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.infra.controller.admin.config.dto.ConfigPageRequest;
import com.focela.platform.module.infra.repository.entity.config.ConfigEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConfigMapper extends BaseMapperX<ConfigEntity> {

    default ConfigEntity selectByKey(String key) {
        return selectOne(ConfigEntity::getConfigKey, key);
    }

    default PageResult<ConfigEntity> selectPage(ConfigPageRequest request) {
        return selectPage(request, new LambdaQueryWrapperX<ConfigEntity>()
                .likeIfPresent(ConfigEntity::getName, request.getName())
                .likeIfPresent(ConfigEntity::getConfigKey, request.getKey())
                .eqIfPresent(ConfigEntity::getType, request.getType())
                .betweenIfPresent(ConfigEntity::getCreateTime, request.getCreateTime())
                .orderByDesc(ConfigEntity::getId));
    }

}
