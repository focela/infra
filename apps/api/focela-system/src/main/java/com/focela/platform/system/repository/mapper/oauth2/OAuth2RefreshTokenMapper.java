package com.focela.platform.system.repository.mapper.oauth2;

import com.focela.platform.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.tenant.core.aop.TenantIgnore;
import com.focela.platform.system.entity.oauth2.OAuth2RefreshTokenEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OAuth2RefreshTokenMapper extends BaseMapperX<OAuth2RefreshTokenEntity> {

    default int deleteByRefreshToken(String refreshToken) {
        return delete(new LambdaQueryWrapperX<OAuth2RefreshTokenEntity>()
                .eq(OAuth2RefreshTokenEntity::getRefreshToken, refreshToken));
    }

    @TenantIgnore // when fetching the token, ignore the tenant ID, because in some scenarios the tenant-id header may not be passed (e.g. file upload, report builder, etc.)
    default OAuth2RefreshTokenEntity selectByRefreshToken(String refreshToken) {
        return selectOne(OAuth2RefreshTokenEntity::getRefreshToken, refreshToken);
    }

}
