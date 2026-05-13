package com.focela.platform.module.system.controller.admin.notify.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Admin - notify message Response VO")
@Data
public class NotifyMessageResponse {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "User ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "25025")
    private Long userId;

    @Schema(description = "User type, see UserTypeEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Byte userType;

    @Schema(description = "Template ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "13013")
    private Long templateId;

    @Schema(description = "Template code", requiredMode = Schema.RequiredMode.REQUIRED, example = "test_01")
    private String templateCode;

    @Schema(description = "Sender name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Alice")
    private String templateNickname;

    @Schema(description = "Template content", requiredMode = Schema.RequiredMode.REQUIRED, example = "test content")
    private String templateContent;

    @Schema(description = "Template type", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer templateType;

    @Schema(description = "template param", requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<String, Object> templateParams;

    @Schema(description = "Is read", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean readStatus;

    @Schema(description = "read time")
    private LocalDateTime readTime;

    @Schema(description = "Created time", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
