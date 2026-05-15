package com.focela.platform.system.controller.admin.dictionary.dto.type;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Admin - dictionary type simplified info Response VO")
@Data
public class DictionaryTypeSimpleResponse {

    @Schema(description = "Dictionary type ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "dictionary type name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Acme")
    private String name;

    @Schema(description = "Dictionary type", requiredMode = Schema.RequiredMode.REQUIRED, example = "sys_common_sex")
    private String type;

}
