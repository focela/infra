package com.focela.platform.module.infra.controller.admin.job.dto.job;

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
    @NotEmpty(message = "任务名称不能为空")
    private String name;

    @Schema(description = "Handler name", requiredMode = Schema.RequiredMode.REQUIRED, example = "sysUserSessionTimeoutJob")
    @NotEmpty(message = "处理器的名字不能为空")
    private String handlerName;

    @Schema(description = "Handler params", example = "yudao")
    private String handlerParam;

    @Schema(description = "CRON expression", requiredMode = Schema.RequiredMode.REQUIRED, example = "0/10 * * * * ? *")
    @NotEmpty(message = "CRON 表达式不能为空")
    private String cronExpression;

    @Schema(description = "Retry count", requiredMode = Schema.RequiredMode.REQUIRED, example = "3")
    @NotNull(message = "重试次数不能为空")
    private Integer retryCount;

    @Schema(description = "Retry interval", requiredMode = Schema.RequiredMode.REQUIRED, example = "1000")
    @NotNull(message = "重试间隔不能为空")
    private Integer retryInterval;

    @Schema(description = "Monitor timeout", example = "1000")
    private Integer monitorTimeout;

}
