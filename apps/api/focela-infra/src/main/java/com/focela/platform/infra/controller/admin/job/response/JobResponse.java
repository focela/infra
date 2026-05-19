package com.focela.platform.infra.controller.admin.job.response;

import com.focela.platform.excel.core.annotations.DictionaryFormat;
import com.focela.platform.excel.core.converter.DictionaryConverter;
import com.focela.platform.infra.constants.DictionaryTypeConstants;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "Admin - scheduled job Response")
@Data
@ExcelIgnoreUnannotated
public class JobResponse {

    @Schema(description = "Job ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("Job ID")
    private Long id;

    @Schema(description = "Job name", requiredMode = Schema.RequiredMode.REQUIRED, example = "test job")
    @ExcelProperty("Job name")
    private String name;

    @Schema(description = "Job status", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "Job status", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.JOB_STATUS)
    private Integer status;

    @Schema(description = "Handler name", requiredMode = Schema.RequiredMode.REQUIRED, example = "sysUserSessionTimeoutJob")
    @ExcelProperty("Handler name")
    private String handlerName;

    @Schema(description = "Handler params", example = "focela")
    @ExcelProperty("Handler params")
    private String handlerParam;

    @Schema(description = "CRON expression", requiredMode = Schema.RequiredMode.REQUIRED, example = "0/10 * * * * ? *")
    @ExcelProperty("CRON expression")
    private String cronExpression;

    @Schema(description = "Retry count", requiredMode = Schema.RequiredMode.REQUIRED, example = "3")
    @NotNull(message = "retry count must not be blank")
    private Integer retryCount;

    @Schema(description = "Retry interval", requiredMode = Schema.RequiredMode.REQUIRED, example = "1000")
    private Integer retryInterval;

    @Schema(description = "Monitor timeout", example = "1000")
    @ExcelProperty("Monitor timeout")
    private Integer monitorTimeout;

    @Schema(description = "Created time", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("Created time")
    private LocalDateTime createTime;

}
