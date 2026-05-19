package com.focela.platform.system.converter.oauth2;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.focela.platform.common.core.KeyValue;
import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.common.utils.collection.CollectionUtils;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.security.core.utils.SecurityFrameworkUtils;
import com.focela.platform.system.controller.admin.oauth2.response.open.OAuth2OpenAccessTokenResponse;
import com.focela.platform.system.controller.admin.oauth2.response.open.OAuth2OpenAuthorizeInfoResponse;
import com.focela.platform.system.controller.admin.oauth2.response.open.OAuth2OpenCheckTokenResponse;
import com.focela.platform.system.domain.entity.oauth2.OAuth2AccessTokenEntity;
import com.focela.platform.system.domain.entity.oauth2.OAuth2ApproveEntity;
import com.focela.platform.system.domain.entity.oauth2.OAuth2ClientEntity;
import com.focela.platform.system.config.oauth2.OAuth2Utils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper
public interface OAuth2OpenConverter {

    OAuth2OpenConverter INSTANCE = Mappers.getMapper(OAuth2OpenConverter.class);

    default OAuth2OpenAccessTokenResponse convert(OAuth2AccessTokenEntity bean) {
        OAuth2OpenAccessTokenResponse response = BeanUtils.toBean(bean, OAuth2OpenAccessTokenResponse.class);
        response.setTokenType(SecurityFrameworkUtils.AUTHORIZATION_BEARER.toLowerCase());
        response.setExpiresIn(OAuth2Utils.getExpiresIn(bean.getExpiresTime()));
        response.setScope(OAuth2Utils.buildScopeString(bean.getScopes()));
        return response;
    }

    default OAuth2OpenCheckTokenResponse convertToCheckTokenResponse(OAuth2AccessTokenEntity bean) {
        OAuth2OpenCheckTokenResponse response = BeanUtils.toBean(bean, OAuth2OpenCheckTokenResponse.class);
        response.setExp(LocalDateTimeUtil.toEpochMilli(bean.getExpiresTime()) / 1000L);
        response.setUserType(UserTypeEnum.ADMIN.getValue());
        return response;
    }

    default OAuth2OpenAuthorizeInfoResponse convert(OAuth2ClientEntity client, List<OAuth2ApproveEntity> approves) {
        // build scopes
        List<KeyValue<String, Boolean>> scopes = new ArrayList<>(client.getScopes().size());
        Map<String, OAuth2ApproveEntity> approveMap = CollectionUtils.convertMap(approves, OAuth2ApproveEntity::getScope);
        client.getScopes().forEach(scope -> {
            OAuth2ApproveEntity approve = approveMap.get(scope);
            scopes.add(new KeyValue<>(scope, approve != null ? approve.getApproved() : false));
        });
        // assemble and return
        return new OAuth2OpenAuthorizeInfoResponse(
                new OAuth2OpenAuthorizeInfoResponse.Client(client.getName(), client.getLogo()), scopes);
    }

}
