package com.focela.platform.system.controller.admin.permission.response.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Admin - menu simplified info Response")
@Data
public class MenuSimpleResponse {

    @Schema(description = "Menu ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "Menu name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Acme")
    private String name;

    @Schema(description = "Parent menu ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long parentId;

    @Schema(description = "Type, see MenuTypeEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer type;

}
