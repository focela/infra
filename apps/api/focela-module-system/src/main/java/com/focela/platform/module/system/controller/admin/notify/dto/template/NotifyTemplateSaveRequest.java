package com.focela.platform.module.system.controller.admin.notify.dto.template;

import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Admin - notify template create /update Request VO")
@Data
public class NotifyTemplateSaveRequest {

    @Schema(description = "ID", example = "1024")
    private Long id;

    @Schema(description = "Template name", requiredMode = Schema.RequiredMode.REQUIRED, example = "test template")
    @NotEmpty(message = "template name must not be blank")
    private String name;

    @Schema(description = "Template code", requiredMode = Schema.RequiredMode.REQUIRED, example = "SEND_TEST")
    @NotNull(message = "template code must not be blank")
    private String code;

    @Schema(description = "Template type (system_notify_template_type dictionary)", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "template type must not be blank")
    private Integer type;

    @Schema(description = "Sender name", requiredMode = Schema.RequiredMode.REQUIRED, example = "potato")
    @NotEmpty(message = "sender name must not be blank")
    private String nickname;

    @Schema(description = "Template content", requiredMode = Schema.RequiredMode.REQUIRED, example = "I am template content")
    @NotEmpty(message = "template content must not be blank")
    private String content;

    @Schema(description = "Status, see CommonStatusEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "status must not be blank")
    @InEnum(value = CommonStatusEnum.class, message = "status must be {value}")
    private Integer status;

    @Schema(description = "Remarks", example = "I am remarks")
    private String remark;

}
