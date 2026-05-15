package com.focela.platform.common.api.system.dictionary.dto;

import com.focela.platform.common.enums.CommonStatusEnum;
import lombok.Data;

/**
 * Dictionary data Response DTO.
 */
@Data
public class DictionaryDataRpcResponse {

    /**
     * Dictionary label.
     */
    private String label;
    /**
     * Dictionary value.
     */
    private String value;
    /**
     * Dictionary type.
     */
    private String dictType;
    /**
     * Status.
     *
     * See {@link CommonStatusEnum}.
     */
    private Integer status;

}
