package com.focela.platform.system.controller.admin.department.response.dept;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Admin - department simplified info Response")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentSimpleResponse {

    @Schema(description = "Department ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "Department name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Acme")
    private String name;

    @Schema(description = "Parent department ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long parentId;

}
