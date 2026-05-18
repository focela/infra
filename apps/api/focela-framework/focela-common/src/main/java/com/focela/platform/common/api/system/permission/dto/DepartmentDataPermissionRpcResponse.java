package com.focela.platform.common.api.system.permission.dto;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * Department data permission response.
 */
@Data
public class DepartmentDataPermissionRpcResponse {

    /**
     * Whether the user can view all data.
     */
    private Boolean all;
    /**
     * Whether the user can view their own data.
     */
    private Boolean self;
    /**
     * IDs of departments the user can view.
     */
    private Set<Long> deptIds;

    public DepartmentDataPermissionRpcResponse() {
        this.all = false;
        this.self = false;
        this.deptIds = new HashSet<>();
    }

}
