package com.focela.platform.system.controller.admin.notify.request.template;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

@Schema(description = "Admin - notify template send Request")
@Data
public class NotifyTemplateSendRequest {

    @Schema(description = "user id", requiredMode = Schema.RequiredMode.REQUIRED, example = "01")
    @NotNull(message = "user idmust not be blank")
    private Long userId;

    @Schema(description = "User type", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "user type must not be blank")
    private Integer userType;

    @Schema(description = "Template code", requiredMode = Schema.RequiredMode.REQUIRED, example = "01")
    @NotEmpty(message = "template code must not be blank")
    private String templateCode;

    @Schema(description = "Template params")
    private Map<String, Object> templateParams;

}
