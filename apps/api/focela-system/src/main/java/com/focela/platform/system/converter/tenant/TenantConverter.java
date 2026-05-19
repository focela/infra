package com.focela.platform.system.converter.tenant;

import com.focela.platform.system.controller.admin.tenant.request.TenantSaveRequest;
import com.focela.platform.system.controller.admin.user.request.UserSaveRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Tenant Convert
 */
@Mapper
public interface TenantConverter {

    TenantConverter INSTANCE = Mappers.getMapper(TenantConverter.class);

    default UserSaveRequest convertToTenantAdminUserRequest(TenantSaveRequest bean) {
        UserSaveRequest request = new UserSaveRequest();
        request.setUsername(bean.getUsername());
        request.setPassword(bean.getPassword());
        request.setNickname(bean.getContactName()).setMobile(bean.getContactMobile());
        return request;
    }

}
