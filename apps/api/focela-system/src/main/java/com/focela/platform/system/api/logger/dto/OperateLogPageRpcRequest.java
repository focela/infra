package com.focela.platform.system.api.logger.dto;

import com.focela.platform.framework.common.model.PageParam;
import lombok.Data;

/**
 * Operate log page Request DTO
 */
@Data
public class OperateLogPageRpcRequest extends PageParam {

    /**
     * Module type
     */
    private String type;
    /**
     * Module data ID
     */
    private Long bizId;

    /**
     * User ID
     */
    private Long userId;

}
