package com.focela.platform.module.system.controller.admin.notify.dto.template;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

@Schema(description = "Admin - notify template send Request VO")
@Data
public class NotifyTemplateSendRequest {

    @Schema(description = "user id", requiredMode = Schema.RequiredMode.REQUIRED, example = "01")
    @NotNull(message = "用户id不能为空")
    private Long userId;

    @Schema(description = "User type", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "用户类型不能为空")
    private Integer userType;

    @Schema(description = "Template code", requiredMode = Schema.RequiredMode.REQUIRED, example = "01")
    @NotEmpty(message = "模板编码不能为空")
    private String templateCode;

    @Schema(description = "Template params")
    private Map<String, Object> templateParams;

}
