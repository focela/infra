package com.focela.platform.module.system.repository.mapper.oauth2;

import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.framework.tenant.core.aop.TenantIgnore;
import com.focela.platform.module.system.controller.admin.oauth2.dto.token.OAuth2AccessTokenPageRequest;
import com.focela.platform.module.system.repository.entity.oauth2.OAuth2AccessTokenEntity;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OAuth2AccessTokenMapper extends BaseMapperX<OAuth2AccessTokenEntity> {

    @TenantIgnore // 获取 token 的时候，需要忽略租户编号。原因是：一些场景下，可能不会传递 tenant-id 请求头，例如说文件上传、积木报表等等
    default OAuth2AccessTokenEntity selectByAccessToken(String accessToken) {
        return selectOne(OAuth2AccessTokenEntity::getAccessToken, accessToken);
    }

    default List<OAuth2AccessTokenEntity> selectListByRefreshToken(String refreshToken) {
        return selectList(OAuth2AccessTokenEntity::getRefreshToken, refreshToken);
    }

    default PageResult<OAuth2AccessTokenEntity> selectPage(OAuth2AccessTokenPageRequest request) {
        return selectPage(request, new LambdaQueryWrapperX<OAuth2AccessTokenEntity>()
                .eqIfPresent(OAuth2AccessTokenEntity::getUserId, request.getUserId())
                .eqIfPresent(OAuth2AccessTokenEntity::getUserType, request.getUserType())
                .likeIfPresent(OAuth2AccessTokenEntity::getClientId, request.getClientId())
                .gt(OAuth2AccessTokenEntity::getExpiresTime, LocalDateTime.now())
                .orderByDesc(OAuth2AccessTokenEntity::getId));
    }

    default List<OAuth2AccessTokenEntity> selectListByUserIdAndUserType(Long userId, Integer userType) {
        return selectList(OAuth2AccessTokenEntity::getUserId, userId,
                OAuth2AccessTokenEntity::getUserType, userType);
    }

}
