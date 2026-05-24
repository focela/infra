package com.focela.platform.common.api.system.dictionary.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.focela.platform.common.enums.CommonStatusEnum;
import lombok.Data;

/**
 * Dictionary data response.
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

    @JsonIgnore
    public String getDictionaryType() {
        return dictType;
    }

    @JsonIgnore
    public DictionaryDataRpcResponse setDictionaryType(String dictionaryType) {
        this.dictType = dictionaryType;
        return this;
    }

}
