package com.focela.platform.system.enums.permission;

import com.focela.platform.framework.common.utils.object.ObjectUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Role code enum
 */
@Getter
@AllArgsConstructor
public enum RoleCodeEnum {

    SUPER_ADMIN("super_admin", "Super Admin"),
    TENANT_ADMIN("tenant_admin", "Tenant Admin"),
    CRM_ADMIN("crm_admin", "CRM Admin"); // CRM system only
    ;

    /**
     * Role code
     */
    private final String code;
    /**
     * Name
     */
    private final String name;

    public static boolean isSuperAdmin(String code) {
        return ObjectUtils.equalsAny(code, SUPER_ADMIN.getCode());
    }

}
