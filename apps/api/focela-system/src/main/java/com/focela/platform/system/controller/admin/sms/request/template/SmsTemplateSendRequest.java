package com.focela.platform.system.controller.admin.sms.request.template;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

@Schema(description = "Admin - SMS template send Request")
@Data
public class SmsTemplateSendRequest {

    @Schema(description = "Mobile number", requiredMode = Schema.RequiredMode.REQUIRED, example = "15601691300")
    @NotNull(message = "mobile number must not be blank")
    private String mobile;

    @Schema(description = "Template code", requiredMode = Schema.RequiredMode.REQUIRED, example = "test_01")
    @NotNull(message = "template code must not be blank")
    private String templateCode;

    @Schema(description = "Template params")
    private Map<String, Object> templateParams;

}
