package com.focela.platform.module.system.converter.oauth2;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.focela.platform.framework.common.core.KeyValue;
import com.focela.platform.framework.common.enums.UserTypeEnum;
import com.focela.platform.framework.common.utils.collection.CollectionUtils;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.framework.security.core.utils.SecurityFrameworkUtils;
import com.focela.platform.module.system.controller.admin.oauth2.dto.open.OAuth2OpenAccessTokenResponse;
import com.focela.platform.module.system.controller.admin.oauth2.dto.open.OAuth2OpenAuthorizeInfoResponse;
import com.focela.platform.module.system.controller.admin.oauth2.dto.open.OAuth2OpenCheckTokenResponse;
import com.focela.platform.module.system.repository.entity.oauth2.OAuth2AccessTokenEntity;
import com.focela.platform.module.system.repository.entity.oauth2.OAuth2ApproveEntity;
import com.focela.platform.module.system.repository.entity.oauth2.OAuth2ClientEntity;
import com.focela.platform.module.system.framework.oauth2.OAuth2Utils;
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
        response.setScope(OAuth2Utils.buildScopeStr(bean.getScopes()));
        return response;
    }

    default OAuth2OpenCheckTokenResponse convert2(OAuth2AccessTokenEntity bean) {
        OAuth2OpenCheckTokenResponse response = BeanUtils.toBean(bean, OAuth2OpenCheckTokenResponse.class);
        response.setExp(LocalDateTimeUtil.toEpochMilli(bean.getExpiresTime()) / 1000L);
        response.setUserType(UserTypeEnum.ADMIN.getValue());
        return response;
    }

    default OAuth2OpenAuthorizeInfoResponse convert(OAuth2ClientEntity client, List<OAuth2ApproveEntity> approves) {
        // 构建 scopes
        List<KeyValue<String, Boolean>> scopes = new ArrayList<>(client.getScopes().size());
        Map<String, OAuth2ApproveEntity> approveMap = CollectionUtils.convertMap(approves, OAuth2ApproveEntity::getScope);
        client.getScopes().forEach(scope -> {
            OAuth2ApproveEntity approve = approveMap.get(scope);
            scopes.add(new KeyValue<>(scope, approve != null ? approve.getApproved() : false));
        });
        // 拼接返回
        return new OAuth2OpenAuthorizeInfoResponse(
                new OAuth2OpenAuthorizeInfoResponse.Client(client.getName(), client.getLogo()), scopes);
    }

}
