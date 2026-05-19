package com.focela.platform.system.controller.admin.dictionary.response.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Admin - data dictionary simplified Response")
@Data
public class DictionaryDataSimpleResponse {

    @Schema(description = "Dictionary type", requiredMode = Schema.RequiredMode.REQUIRED, example = "gender")
    private String dictType;

    @Schema(description = "dictionary value", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private String value;

    @Schema(description = "Dictionary label", requiredMode = Schema.RequiredMode.REQUIRED, example = "male")
    private String label;

    @Schema(description = "color type, default, primary, success, info, warning, danger", example = "default")
    private String colorType;

    @Schema(description = "CSS style", example = "btn-visible")
    private String cssClass;

}
