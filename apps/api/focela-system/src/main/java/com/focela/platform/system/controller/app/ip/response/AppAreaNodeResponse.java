package com.focela.platform.system.controller.app.ip.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "User App - area node Response")
@Data
public class AppAreaNodeResponse {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "110000")
    private Integer id;

    @Schema(description = "Name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Beijing")
    private String name;

    /**
     * Child nodes
     */
    private List<AppAreaNodeResponse> children;

}
