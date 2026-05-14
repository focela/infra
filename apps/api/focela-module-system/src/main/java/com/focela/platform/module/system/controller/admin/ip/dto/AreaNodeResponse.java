package com.focela.platform.module.system.controller.admin.ip.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "Admin - area node Response VO")
@Data
public class AreaNodeResponse {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "110000")
    private Integer id;

    @Schema(description = "Name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Beijing")
    private String name;

    /**
     * child nodes
     */
    private List<AreaNodeResponse> children;

}
