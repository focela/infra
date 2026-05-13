package com.focela.platform.module.infra.controller.admin.job.dto.log;

import com.focela.platform.framework.excel.core.annotations.DictionaryFormat;
import com.focela.platform.framework.excel.core.converter.DictionaryConverter;
import com.focela.platform.module.infra.enums.DictionaryTypeConstants;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "Admin - scheduled job log Response VO")
@Data
@ExcelIgnoreUnannotated
public class JobLogResponse {

    @Schema(description = "Log ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("日志编号")
    private Long id;

    @Schema(description = "Job ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("任务编号")
    private Long jobId;

    @Schema(description = "Handler name", requiredMode = Schema.RequiredMode.REQUIRED, example = "sysUserSessionTimeoutJob")
    @ExcelProperty("处理器的名字")
    private String handlerName;

    @Schema(description = "Handler params", example = "yudao")
    @ExcelProperty("处理器的参数")
    private String handlerParam;

    @Schema(description = "attempt number execute", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("第几次执行")
    private Integer executeIndex;

    @Schema(description = "Execution start time", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("开始执行时间")
    private LocalDateTime beginTime;

    @Schema(description = "Execution end time")
    @ExcelProperty("结束执行时间")
    private LocalDateTime endTime;

    @Schema(description = "Execution duration", example = "123")
    @ExcelProperty("执行时长")
    private Integer duration;

    @Schema(description = "Job status, see JobLogStatusEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "任务状态", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.JOB_LOG_STATUS)
    private Integer status;

    @Schema(description = "result data", example = "execute success")
    @ExcelProperty("结果数据")
    private String result;

    @Schema(description = "Created time", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
