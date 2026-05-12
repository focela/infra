package com.focela.platform.module.system.controller.admin.dictionary.dto.type;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Admin - dictionary type create /update Request VO")
@Data
public class DictionaryTypeSaveRequest {

    @Schema(description = "Dictionary type ID", example = "1024")
    private Long id;

    @Schema(description = "Dictionary name", requiredMode = Schema.RequiredMode.REQUIRED, example = "gender")
    @NotBlank(message = "字典名称不能为空")
    @Size(max = 100, message = "字典类型名称长度不能超过100个字符")
    private String name;

    @Schema(description = "Dictionary type", requiredMode = Schema.RequiredMode.REQUIRED, example = "sys_common_sex")
    @NotNull(message = "字典类型不能为空")
    @Size(max = 100, message = "字典类型类型长度不能超过 100 个字符")
    private String type;

    @Schema(description = "Status, see CommonStatusEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "Remarks", example = "happy remarks")
    private String remark;

}
