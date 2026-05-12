package com.focela.platform.module.system.repository.mapper.social;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.system.controller.admin.socail.dto.client.SocialClientPageRequest;
import com.focela.platform.module.system.repository.entity.social.SocialClientEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SocialClientMapper extends BaseMapperX<SocialClientEntity> {

    default SocialClientEntity selectBySocialTypeAndUserType(Integer socialType, Integer userType) {
        return selectOne(SocialClientEntity::getSocialType, socialType,
                SocialClientEntity::getUserType, userType);
    }

    default PageResult<SocialClientEntity> selectPage(SocialClientPageRequest reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SocialClientEntity>()
                .likeIfPresent(SocialClientEntity::getName, reqVO.getName())
                .eqIfPresent(SocialClientEntity::getSocialType, reqVO.getSocialType())
                .eqIfPresent(SocialClientEntity::getUserType, reqVO.getUserType())
                .likeIfPresent(SocialClientEntity::getClientId, reqVO.getClientId())
                .eqIfPresent(SocialClientEntity::getStatus, reqVO.getStatus())
                .orderByDesc(SocialClientEntity::getId));
    }

}
