package com.focela.platform.module.system.controller.admin.dictionary.dto.data;

import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.common.model.PageParam;
import com.focela.platform.framework.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.Size;

@Schema(description = "Admin - Dictionary type page request")
@Data
@EqualsAndHashCode(callSuper = true)
public class DictionaryDataPageRequest extends PageParam {

    @Schema(description = "Dictionary label", example = "Acme")
    @Size(max = 100, message = "字典标签长度不能超过100个字符")
    private String label;

    @Schema(description = "Dictionary type (fuzzy match)", example = "sys_common_sex")
    @Size(max = 100, message = "字典类型类型长度不能超过100个字符")
    private String dictType;

    @Schema(description = "Display status, see CommonStatusEnum", example = "1")
    @InEnum(value = CommonStatusEnum.class, message = "修改状态必须是 {value}")
    private Integer status;

}
