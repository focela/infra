package com.focela.platform.module.system.controller.admin.department.dto.post;

import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Admin - post info simplified Response VO")
@Data
public class PostSimpleResponse {

    @Schema(description = "Post order", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("Post ID")
    private Long id;

    @Schema(description = "Post name", requiredMode = Schema.RequiredMode.REQUIRED, example = "little potato")
    @ExcelProperty("Post Name")
    private String name;

}
