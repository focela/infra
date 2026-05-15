package com.focela.platform.module.system.converter.tenant;

import com.focela.platform.module.system.controller.admin.tenant.dto.TenantSaveRequest;
import com.focela.platform.module.system.controller.admin.user.dto.UserSaveRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 租户 Convert
 */
@Mapper
public interface TenantConverter {

    TenantConverter INSTANCE = Mappers.getMapper(TenantConverter.class);

    default UserSaveRequest convert02(TenantSaveRequest bean) {
        UserSaveRequest request = new UserSaveRequest();
        request.setUsername(bean.getUsername());
        request.setPassword(bean.getPassword());
        request.setNickname(bean.getContactName()).setMobile(bean.getContactMobile());
        return request;
    }

}
