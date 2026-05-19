package com.focela.platform.system.controller.admin.notice.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "Admin - notice info Response VO")
@Data
public class NoticeResponse {

    @Schema(description = "Notice order", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "Notice title", requiredMode = Schema.RequiredMode.REQUIRED, example = "XiaoBo master")
    private String title;

    @Schema(description = "Notice type", requiredMode = Schema.RequiredMode.REQUIRED, example = "XiaoBo master")
    private Integer type;

    @Schema(description = "Notice content", requiredMode = Schema.RequiredMode.REQUIRED, example = "Acme")
    private String content;

    @Schema(description = "Status, see CommonStatusEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "Created time", requiredMode = Schema.RequiredMode.REQUIRED, example = "timestamp format")
    private LocalDateTime createTime;

}
