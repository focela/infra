package com.focela.platform.infra.controller.admin.database.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "Admin - datasource config create /update Request")
@Data
public class DataSourceConfigSaveRequest {

    @Schema(description = "Primary key ID", example = "1024")
    private Long id;

    @Schema(description = "Datasource name", requiredMode = Schema.RequiredMode.REQUIRED, example = "test")
    @NotNull(message = "datasource name must not be blank")
    private String name;

    @Schema(description = "Datasource URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "jdbc:mysql://127.0.0.1:3306/ruoyi-vue-pro")
    @NotNull(message = "datasource URL must not be blank")
    private String url;

    @Schema(description = "Username", requiredMode = Schema.RequiredMode.REQUIRED, example = "root")
    @NotNull(message = "username must not be blank")
    private String username;

    @Schema(description = "Password", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @NotNull(message = "password must not be blank")
    private String password;

}
