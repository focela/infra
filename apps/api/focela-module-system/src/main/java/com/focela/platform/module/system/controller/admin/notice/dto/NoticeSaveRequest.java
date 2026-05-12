package com.focela.platform.module.system.controller.admin.notice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Admin - notice create /update Request VO")
@Data
public class NoticeSaveRequest {

    @Schema(description = "post notice ID", example = "1024")
    private Long id;

    @Schema(description = "Notice title", requiredMode = Schema.RequiredMode.REQUIRED, example = "XiaoBo master")
    @NotBlank(message = "公告标题不能为空")
    @Size(max = 50, message = "公告标题不能超过50个字符")
    private String title;

    @Schema(description = "Notice type", requiredMode = Schema.RequiredMode.REQUIRED, example = "XiaoBo master")
    @NotNull(message = "公告类型不能为空")
    private Integer type;

    @Schema(description = "Notice content", requiredMode = Schema.RequiredMode.REQUIRED, example = "Acme")
    private String content;

    @Schema(description = "Status, see CommonStatusEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

}
