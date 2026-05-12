package com.focela.platform.module.system.repository.mapper.social;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.system.controller.admin.socail.dto.user.SocialUserPageRequest;
import com.focela.platform.module.system.repository.entity.social.SocialUserEntity;
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

    default PageResult<SocialUserEntity> selectPage(SocialUserPageRequest reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SocialUserEntity>()
                .eqIfPresent(SocialUserEntity::getType, reqVO.getType())
                .likeIfPresent(SocialUserEntity::getNickname, reqVO.getNickname())
                .likeIfPresent(SocialUserEntity::getOpenid, reqVO.getOpenid())
                .betweenIfPresent(SocialUserEntity::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(SocialUserEntity::getId));
    }

}
