package com.focela.platform.system.controller.admin.department.request.dept;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Admin - department list Request VO")
@Data
public class DepartmentListRequest {

    @Schema(description = "department name, fuzzy match", example = "Acme")
    private String name;

    @Schema(description = "Display status, see CommonStatusEnum", example = "1")
    private Integer status;

}
