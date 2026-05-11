package com.focela.platform.module.infra.repository.mapper.config;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.infra.controller.admin.config.vo.ConfigPageReqVO;
import com.focela.platform.module.infra.repository.entity.config.ConfigEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConfigMapper extends BaseMapperX<ConfigEntity> {

    default ConfigEntity selectByKey(String key) {
        return selectOne(ConfigEntity::getConfigKey, key);
    }

    default PageResult<ConfigEntity> selectPage(ConfigPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ConfigEntity>()
                .likeIfPresent(ConfigEntity::getName, reqVO.getName())
                .likeIfPresent(ConfigEntity::getConfigKey, reqVO.getKey())
                .eqIfPresent(ConfigEntity::getType, reqVO.getType())
                .betweenIfPresent(ConfigEntity::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ConfigEntity::getId));
    }

}
