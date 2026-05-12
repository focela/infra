package com.focela.platform.module.system.convert.tenant;

import com.focela.platform.module.system.controller.admin.tenant.dto.tenant.TenantSaveRequest;
import com.focela.platform.module.system.controller.admin.user.dto.user.UserSaveRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 租户 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface TenantConvert {

    TenantConvert INSTANCE = Mappers.getMapper(TenantConvert.class);

    default UserSaveRequest convert02(TenantSaveRequest bean) {
        UserSaveRequest request = new UserSaveRequest();
        request.setUsername(bean.getUsername());
        request.setPassword(bean.getPassword());
        request.setNickname(bean.getContactName()).setMobile(bean.getContactMobile());
        return request;
    }

}
