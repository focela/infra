package com.focela.platform.system.controller.admin.mail.dto.template;

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
    @NotNull(message = "name must not be blank")
    private String name;

    @Schema(description = "Template ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "test")
    @NotNull(message = "template ID must not be blank")
    private String code;

    @Schema(description = "Sender email account ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "send email account ID must not be blank")
    private Long accountId;

    @Schema(description = "Sender name", example = "Bob")
    private String nickname;

    @Schema(description = "Title", requiredMode = Schema.RequiredMode.REQUIRED, example = "register success")
    @NotEmpty(message = "title must not be blank")
    private String title;

    @Schema(description = "Content", requiredMode = Schema.RequiredMode.REQUIRED, example = "hello, register success")
    @NotEmpty(message = "content must not be blank")
    private String content;

    @Schema(description = "Status, see CommonStatusEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "status must not be blank")
    private Integer status;

    @Schema(description = "Remarks", example = "Ultraman")
    private String remark;

}
