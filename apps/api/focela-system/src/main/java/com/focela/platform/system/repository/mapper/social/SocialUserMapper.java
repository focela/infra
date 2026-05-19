package com.focela.platform.system.repository.mapper.social;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.system.controller.admin.social.dto.user.SocialUserPageRequest;
import com.focela.platform.system.domain.entity.social.SocialUserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SocialUserMapper extends BaseMapperX<SocialUserEntity> {

    default SocialUserEntity selectByTypeAndCodeAnState(Integer type, String code, String state) {
        return selectOne(SocialUserEntity::getType, type,
                SocialUserEntity::getCode, code,
                SocialUserEntity::getState, state);
    }

    default SocialUserEntity selectByTypeAndOpenid(Integer type, String openid) {
        return selectFirstOne(SocialUserEntity::getType, type,
                SocialUserEntity::getOpenid, openid);
    }

    default PageResult<SocialUserEntity> selectPage(SocialUserPageRequest request) {
        return selectPage(request, new LambdaQueryWrapperX<SocialUserEntity>()
                .eqIfPresent(SocialUserEntity::getType, request.getType())
                .likeIfPresent(SocialUserEntity::getNickname, request.getNickname())
                .likeIfPresent(SocialUserEntity::getOpenid, request.getOpenid())
                .betweenIfPresent(SocialUserEntity::getCreateTime, request.getCreateTime())
                .orderByDesc(SocialUserEntity::getId));
    }

}
