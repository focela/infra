package com.focela.platform.module.system.repository.mapper.oauth2;

import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.module.system.repository.entity.oauth2.OAuth2CodeEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OAuth2CodeMapper extends BaseMapperX<OAuth2CodeEntity> {

    default OAuth2CodeEntity selectByCode(String code) {
        return selectOne(OAuth2CodeEntity::getCode, code);
    }

}
