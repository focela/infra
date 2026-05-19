package com.focela.platform.system.repository.mapper.social;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.system.controller.admin.social.request.client.SocialClientPageRequest;
import com.focela.platform.system.domain.entity.social.SocialClientEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SocialClientMapper extends BaseMapperX<SocialClientEntity> {

    default SocialClientEntity selectBySocialTypeAndUserType(Integer socialType, Integer userType) {
        return selectOne(SocialClientEntity::getSocialType, socialType,
                SocialClientEntity::getUserType, userType);
    }

    default PageResult<SocialClientEntity> selectPage(SocialClientPageRequest request) {
        return selectPage(request, new LambdaQueryWrapperX<SocialClientEntity>()
                .likeIfPresent(SocialClientEntity::getName, request.getName())
                .eqIfPresent(SocialClientEntity::getSocialType, request.getSocialType())
                .eqIfPresent(SocialClientEntity::getUserType, request.getUserType())
                .likeIfPresent(SocialClientEntity::getClientId, request.getClientId())
                .eqIfPresent(SocialClientEntity::getStatus, request.getStatus())
                .orderByDesc(SocialClientEntity::getId));
    }

}
