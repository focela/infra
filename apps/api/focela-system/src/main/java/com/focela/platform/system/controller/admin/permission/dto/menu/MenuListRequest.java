package com.focela.platform.system.controller.admin.permission.dto.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Admin - menu list Request VO")
@Data
public class MenuListRequest {

    @Schema(description = "menu name, fuzzy match", example = "Acme")
    private String name;

    @Schema(description = "Display status, see CommonStatusEnum", example = "1")
    private Integer status;

}
