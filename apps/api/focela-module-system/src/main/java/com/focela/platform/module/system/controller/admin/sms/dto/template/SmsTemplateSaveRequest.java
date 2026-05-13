package com.focela.platform.module.system.controller.admin.sms.dto.template;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Schema(description = "Admin - SMS template create /update Request VO")
@Data
public class SmsTemplateSaveRequest {

    @Schema(description = "ID", example = "1024")
    private Long id;

    @Schema(description = "SMS template type, see SmsTemplateTypeEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "SMS type must not be blank")
    private Integer type;

    @Schema(description = "Enable status, see CommonStatusEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "enable status must not be blank")
    private Integer status;

    @Schema(description = "Template code", requiredMode = Schema.RequiredMode.REQUIRED, example = "test_01")
    @NotNull(message = "template code must not be blank")
    private String code;

    @Schema(description = "Template name", requiredMode = Schema.RequiredMode.REQUIRED, example = "yudao")
    @NotNull(message = "template name must not be blank")
    private String name;

    @Schema(description = "Template content", requiredMode = Schema.RequiredMode.REQUIRED, example = "hello, {name}. you tall too {like}!")
    @NotNull(message = "template content must not be blank")
    private String content;

    @Schema(description = "Remarks", example = "")
    private String remark;

    @Schema(description = "SMS API template ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "4383920")
    @NotNull(message = "SMS API template ID must not be blank")
    private String apiTemplateId;

    @Schema(description = "SMS channel ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "SMS channel ID must not be blank")
    private Long channelId;

}
