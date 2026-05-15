package com.focela.platform.system.controller.admin.dictionary.dto.type;

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
    @NotBlank(message = "dictionary name must not be blank")
    @Size(max = 100, message = "dictionary type name length must not exceed 100characters")
    private String name;

    @Schema(description = "Dictionary type", requiredMode = Schema.RequiredMode.REQUIRED, example = "sys_common_sex")
    @NotNull(message = "dictionary type must not be blank")
    @Size(max = 100, message = "dictionary type type length must not exceed 100 characters")
    private String type;

    @Schema(description = "Status, see CommonStatusEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "status must not be blank")
    private Integer status;

    @Schema(description = "Remarks", example = "happy remarks")
    private String remark;

}
