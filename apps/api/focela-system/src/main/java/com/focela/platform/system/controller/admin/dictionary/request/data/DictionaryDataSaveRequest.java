package com.focela.platform.system.controller.admin.dictionary.request.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Admin - dictionary data create/update Request")
@Data
public class DictionaryDataSaveRequest {

    @Schema(description = "Dictionary data ID", example = "1024")
    private Long id;

    @Schema(description = "Display order", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "display order must not be blank")
    private Integer sort;

    @Schema(description = "Dictionary label", requiredMode = Schema.RequiredMode.REQUIRED, example = "Acme")
    @NotBlank(message = "dictionary label must not be blank")
    @Size(max = 100, message = "dictionary label length must not exceed 100characters")
    private String label;

    @Schema(description = "Dictionary value", requiredMode = Schema.RequiredMode.REQUIRED, example = "sample")
    @NotBlank(message = "dictionary value must not be blank")
    @Size(max = 100, message = "dictionary value length must not exceed 100 characters")
    private String value;

    @Schema(description = "Dictionary type", requiredMode = Schema.RequiredMode.REQUIRED, example = "sys_common_sex")
    @NotBlank(message = "dictionary type must not be blank")
    @Size(max = 100, message = "dictionary type length must not exceed 100characters")
    private String dictType;

    @Schema(description = "Status, see CommonStatusEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "status must not be blank")
    @InEnum(value = CommonStatusEnum.class, message = "update status must be {value}")
    private Integer status;

    @Schema(description = "Color type (default/primary/success/info/warning/danger)", example = "default")
    private String colorType;

    @Schema(description = "CSS style", example = "btn-visible")
    private String cssClass;

    @Schema(description = "Remarks", example = "I am a role")
    private String remark;

    @JsonIgnore
    @Schema(hidden = true)
    public String getDictionaryType() {
        return dictType;
    }

    @JsonIgnore
    @Schema(hidden = true)
    public DictionaryDataSaveRequest setDictionaryType(String dictionaryType) {
        this.dictType = dictionaryType;
        return this;
    }

}
