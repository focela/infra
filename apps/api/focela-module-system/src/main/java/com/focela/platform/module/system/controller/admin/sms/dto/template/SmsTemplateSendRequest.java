package com.focela.platform.module.system.controller.admin.sms.dto.template;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

@Schema(description = "Admin - SMS template send Request VO")
@Data
public class SmsTemplateSendRequest {

    @Schema(description = "Mobile number", requiredMode = Schema.RequiredMode.REQUIRED, example = "15601691300")
    @NotNull(message = "手机号不能为空")
    private String mobile;

    @Schema(description = "Template code", requiredMode = Schema.RequiredMode.REQUIRED, example = "test_01")
    @NotNull(message = "模板编码不能为空")
    private String templateCode;

    @Schema(description = "Template params")
    private Map<String, Object> templateParams;

}
