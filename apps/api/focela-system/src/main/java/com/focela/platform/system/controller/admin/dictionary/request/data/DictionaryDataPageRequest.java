package com.focela.platform.system.controller.admin.dictionary.request.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.model.PageParam;
import com.focela.platform.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.Size;

@Schema(description = "Admin - Dictionary type page request")
@Data
@EqualsAndHashCode(callSuper = true)
public class DictionaryDataPageRequest extends PageParam {

    @Schema(description = "Dictionary label", example = "Acme")
    @Size(max = 100, message = "dictionary label length must not exceed 100characters")
    private String label;

    @Schema(description = "Dictionary type (fuzzy match)", example = "sys_common_sex")
    @Size(max = 100, message = "dictionary type type length must not exceed 100characters")
    private String dictType;

    @Schema(description = "Display status, see CommonStatusEnum", example = "1")
    @InEnum(value = CommonStatusEnum.class, message = "update status must be {value}")
    private Integer status;

    @JsonIgnore
    @Schema(hidden = true)
    public String getDictionaryType() {
        return dictType;
    }

    @JsonIgnore
    @Schema(hidden = true)
    public DictionaryDataPageRequest setDictionaryType(String dictionaryType) {
        this.dictType = dictionaryType;
        return this;
    }

}
