package com.focela.platform.module.system.controller.admin.mail.dto.template;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Admin - email template create /update Request VO")
@Data
public class MailTemplateSaveRequest {

    @Schema(description = "ID", example = "1024")
    private Long id;

    @Schema(description = "Template name", requiredMode = Schema.RequiredMode.REQUIRED, example = "test name")
    @NotNull(message = "名称不能为空")
    private String name;

    @Schema(description = "Template ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "test")
    @NotNull(message = "模版编号不能为空")
    private String code;

    @Schema(description = "Sender email account ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "发送的邮箱账号编号不能为空")
    private Long accountId;

    @Schema(description = "Sender name", example = "Bob")
    private String nickname;

    @Schema(description = "Title", requiredMode = Schema.RequiredMode.REQUIRED, example = "register success")
    @NotEmpty(message = "标题不能为空")
    private String title;

    @Schema(description = "Content", requiredMode = Schema.RequiredMode.REQUIRED, example = "hello, register success")
    @NotEmpty(message = "内容不能为空")
    private String content;

    @Schema(description = "Status, see CommonStatusEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "Remarks", example = "Ultraman")
    private String remark;

}
