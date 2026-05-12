package com.focela.platform.module.system.repository.mapper.oauth2;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.system.controller.admin.oauth2.dto.client.OAuth2ClientPageRequest;
import com.focela.platform.module.system.repository.entity.oauth2.OAuth2ClientEntity;
import org.apache.ibatis.annotations.Mapper;


/**
 * OAuth2 客户端 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface OAuth2ClientMapper extends BaseMapperX<OAuth2ClientEntity> {

    default PageResult<OAuth2ClientEntity> selectPage(OAuth2ClientPageRequest request) {
        return selectPage(request, new LambdaQueryWrapperX<OAuth2ClientEntity>()
                .likeIfPresent(OAuth2ClientEntity::getName, request.getName())
                .eqIfPresent(OAuth2ClientEntity::getStatus, request.getStatus())
                .orderByDesc(OAuth2ClientEntity::getId));
    }

    default OAuth2ClientEntity selectByClientId(String clientId) {
        return selectOne(OAuth2ClientEntity::getClientId, clientId);
    }

}
