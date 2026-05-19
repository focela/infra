package com.focela.platform.infra.controller.admin.job.response.log;

import com.focela.platform.excel.core.annotations.DictionaryFormat;
import com.focela.platform.excel.core.converter.DictionaryConverter;
import com.focela.platform.infra.constants.DictionaryTypeConstants;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "Admin - scheduled job log Response")
@Data
@ExcelIgnoreUnannotated
public class JobLogResponse {

    @Schema(description = "Log ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("Log ID")
    private Long id;

    @Schema(description = "Job ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("Job ID")
    private Long jobId;

    @Schema(description = "Handler name", requiredMode = Schema.RequiredMode.REQUIRED, example = "sysUserSessionTimeoutJob")
    @ExcelProperty("Handler name")
    private String handlerName;

    @Schema(description = "Handler params", example = "focela")
    @ExcelProperty("Handler params")
    private String handlerParam;

    @Schema(description = "attempt number execute", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("Execute index")
    private Integer executeIndex;

    @Schema(description = "Execution start time", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("Execution start time")
    private LocalDateTime beginTime;

    @Schema(description = "Execution end time")
    @ExcelProperty("Execution end time")
    private LocalDateTime endTime;

    @Schema(description = "Execution duration", example = "123")
    @ExcelProperty("Execution duration")
    private Integer duration;

    @Schema(description = "Job status, see JobLogStatusEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "Job status", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.JOB_LOG_STATUS)
    private Integer status;

    @Schema(description = "result data", example = "execute success")
    @ExcelProperty("Result data")
    private String result;

    @Schema(description = "Created time", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("Created time")
    private LocalDateTime createTime;

}
