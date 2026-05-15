package com.focela.platform.system.api.department.dto;

import com.focela.platform.common.enums.CommonStatusEnum;
import lombok.Data;

/**
 * Post Response DTO
 */
@Data
public class PostRpcResponse {

    /**
     * Post ID
     */
    private Long id;
    /**
     * Post name
     */
    private String name;
    /**
     * Post code
     */
    private String code;
    /**
     * Post sort order
     */
    private Integer sort;
    /**
     * Status
     *
     * Enum {@link CommonStatusEnum}
     */
    private Integer status;

}
