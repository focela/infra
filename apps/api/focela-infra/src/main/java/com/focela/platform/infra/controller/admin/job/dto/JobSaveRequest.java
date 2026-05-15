package com.focela.platform.infra.controller.admin.job.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "Admin - scheduled job create /update Request VO")
@Data
public class JobSaveRequest {

    @Schema(description = "Job ID", example = "1024")
    private Long id;

    @Schema(description = "Job name", requiredMode = Schema.RequiredMode.REQUIRED, example = "test job")
    @NotEmpty(message = "job name must not be blank")
    private String name;

    @Schema(description = "Handler name", requiredMode = Schema.RequiredMode.REQUIRED, example = "sysUserSessionTimeoutJob")
    @NotEmpty(message = "handler name must not be blank")
    private String handlerName;

    @Schema(description = "Handler params", example = "focela")
    private String handlerParam;

    @Schema(description = "CRON expression", requiredMode = Schema.RequiredMode.REQUIRED, example = "0/10 * * * * ? *")
    @NotEmpty(message = "CRON expression must not be blank")
    private String cronExpression;

    @Schema(description = "Retry count", requiredMode = Schema.RequiredMode.REQUIRED, example = "3")
    @NotNull(message = "retry count must not be blank")
    private Integer retryCount;

    @Schema(description = "Retry interval", requiredMode = Schema.RequiredMode.REQUIRED, example = "1000")
    @NotNull(message = "retry interval must not be blank")
    private Integer retryInterval;

    @Schema(description = "Monitor timeout", example = "1000")
    private Integer monitorTimeout;

}
