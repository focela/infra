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
    @NotNull(message = "短信类型不能为空")
    private Integer type;

    @Schema(description = "Enable status, see CommonStatusEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "开启状态不能为空")
    private Integer status;

    @Schema(description = "Template code", requiredMode = Schema.RequiredMode.REQUIRED, example = "test_01")
    @NotNull(message = "模板编码不能为空")
    private String code;

    @Schema(description = "Template name", requiredMode = Schema.RequiredMode.REQUIRED, example = "yudao")
    @NotNull(message = "模板名称不能为空")
    private String name;

    @Schema(description = "Template content", requiredMode = Schema.RequiredMode.REQUIRED, example = "hello, {name}. you tall too {like}!")
    @NotNull(message = "模板内容不能为空")
    private String content;

    @Schema(description = "Remarks", example = "")
    private String remark;

    @Schema(description = "SMS API template ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "4383920")
    @NotNull(message = "短信 API 的模板编号不能为空")
    private String apiTemplateId;

    @Schema(description = "SMS channel ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "短信渠道编号不能为空")
    private Long channelId;

}
