package com.focela.platform.module.system.repository.mapper.oauth2;

import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.framework.tenant.core.aop.TenantIgnore;
import com.focela.platform.module.system.repository.entity.oauth2.OAuth2RefreshTokenEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OAuth2RefreshTokenMapper extends BaseMapperX<OAuth2RefreshTokenEntity> {

    default int deleteByRefreshToken(String refreshToken) {
        return delete(new LambdaQueryWrapperX<OAuth2RefreshTokenEntity>()
                .eq(OAuth2RefreshTokenEntity::getRefreshToken, refreshToken));
    }

    @TenantIgnore // 获取 token 的时候，需要忽略租户编号。原因是：一些场景下，可能不会传递 tenant-id 请求头，例如说文件上传、积木报表等等
    default OAuth2RefreshTokenEntity selectByRefreshToken(String refreshToken) {
        return selectOne(OAuth2RefreshTokenEntity::getRefreshToken, refreshToken);
    }

}
