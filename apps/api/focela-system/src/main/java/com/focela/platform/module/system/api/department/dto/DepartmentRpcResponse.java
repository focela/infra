package com.focela.platform.module.system.api.department.dto;

import com.focela.platform.framework.common.enums.CommonStatusEnum;
import lombok.Data;

/**
 * Department Response DTO
 */
@Data
public class DepartmentRpcResponse {

    /**
     * Department ID
     */
    private Long id;
    /**
     * Department name
     */
    private String name;
    /**
     * Parent department ID
     */
    private Long parentId;
    /**
     * Leader user ID
     */
    private Long leaderUserId;
    /**
     * Department status
     *
     * Enum {@link CommonStatusEnum}
     */
    private Integer status;

}
