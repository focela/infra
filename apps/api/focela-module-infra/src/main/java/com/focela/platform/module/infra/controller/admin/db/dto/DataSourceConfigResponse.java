package com.focela.platform.module.infra.controller.admin.db.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "Admin - datasource config Response VO")
@Data
public class DataSourceConfigResponse {

    @Schema(description = "Primary key ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "Datasource name", requiredMode = Schema.RequiredMode.REQUIRED, example = "test")
    private String name;

    @Schema(description = "Datasource URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "jdbc:mysql://127.0.0.1:3306/ruoyi-vue-pro")
    private String url;

    @Schema(description = "Username", requiredMode = Schema.RequiredMode.REQUIRED, example = "root")
    private String username;

    @Schema(description = "Created time", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
