package com.focela.platform.system.controller.admin.department.dto.dept;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "Admin - department info Response VO")
@Data
public class DepartmentResponse {

    @Schema(description = "Department ID", example = "1024")
    private Long id;

    @Schema(description = "Department name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Acme")
    private String name;

    @Schema(description = "Parent department ID", example = "1024")
    private Long parentId;

    @Schema(description = "Display order", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Integer sort;

    @Schema(description = "Owner user ID", example = "2048")
    private Long leaderUserId;

    @Schema(description = "Contact phone", example = "15601691000")
    private String phone;

    @Schema(description = "Email", example = "user@example.com")
    private String email;

    @Schema(description = "Status, see CommonStatusEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "Created time", requiredMode = Schema.RequiredMode.REQUIRED, example = "timestamp format")
    private LocalDateTime createTime;

}
