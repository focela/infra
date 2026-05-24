package com.focela.platform.system.repository.mapper.oauth2;

import com.focela.platform.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.system.domain.entity.oauth2.OAuth2ApproveEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OAuth2ApproveMapper extends BaseMapperX<OAuth2ApproveEntity> {

    default int update(OAuth2ApproveEntity updateEntity) {
        return update(updateEntity, new LambdaQueryWrapperX<OAuth2ApproveEntity>()
                .eq(OAuth2ApproveEntity::getUserId, updateEntity.getUserId())
                .eq(OAuth2ApproveEntity::getUserType, updateEntity.getUserType())
                .eq(OAuth2ApproveEntity::getClientId, updateEntity.getClientId())
                .eq(OAuth2ApproveEntity::getScope, updateEntity.getScope()));
    }

    default List<OAuth2ApproveEntity> selectListByUserIdAndUserTypeAndClientId(Long userId, Integer userType, String clientId) {
        return selectList(new LambdaQueryWrapperX<OAuth2ApproveEntity>()
                .eq(OAuth2ApproveEntity::getUserId, userId)
                .eq(OAuth2ApproveEntity::getUserType, userType)
                .eq(OAuth2ApproveEntity::getClientId, clientId));
    }

}
