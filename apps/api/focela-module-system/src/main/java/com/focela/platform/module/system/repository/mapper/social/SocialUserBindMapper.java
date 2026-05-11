package com.focela.platform.module.system.repository.mapper.social;

import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.system.repository.entity.social.SocialUserBindEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SocialUserBindMapper extends BaseMapperX<SocialUserBindEntity> {

    default void deleteByUserTypeAndUserIdAndSocialType(Integer userType, Long userId, Integer socialType) {
        delete(new LambdaQueryWrapperX<SocialUserBindEntity>()
                .eq(SocialUserBindEntity::getUserType, userType)
                .eq(SocialUserBindEntity::getUserId, userId)
                .eq(SocialUserBindEntity::getSocialType, socialType));
    }

    default void deleteByUserTypeAndSocialUserId(Integer userType, Long socialUserId) {
        delete(new LambdaQueryWrapperX<SocialUserBindEntity>()
                .eq(SocialUserBindEntity::getUserType, userType)
                .eq(SocialUserBindEntity::getSocialUserId, socialUserId));
    }

    default SocialUserBindEntity selectByUserTypeAndSocialUserId(Integer userType, Long socialUserId) {
        return selectOne(SocialUserBindEntity::getUserType, userType,
                SocialUserBindEntity::getSocialUserId, socialUserId);
    }

    default List<SocialUserBindEntity> selectListByUserIdAndUserType(Long userId, Integer userType) {
        return selectList(new LambdaQueryWrapperX<SocialUserBindEntity>()
                .eq(SocialUserBindEntity::getUserId, userId)
                .eq(SocialUserBindEntity::getUserType, userType));
    }

    default SocialUserBindEntity selectByUserIdAndUserTypeAndSocialType(Long userId, Integer userType, Integer socialType) {
        return selectOne(new LambdaQueryWrapperX<SocialUserBindEntity>()
                .eq(SocialUserBindEntity::getUserId, userId)
                .eq(SocialUserBindEntity::getUserType, userType)
                .eq(SocialUserBindEntity::getSocialType, socialType));
    }

}
