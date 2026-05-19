package com.focela.platform.infra.controller.admin.job.request.log;

import com.focela.platform.common.model.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.focela.platform.common.utils.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "Admin - scheduled job log page Request")
@Data
public class JobLogPageRequest extends PageParam {

    @Schema(description = "Job ID", example = "10")
    private Long jobId;

    @Schema(description = "Handler name (fuzzy match)")
    private String handlerName;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "Execution start time")
    private LocalDateTime beginTime;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "Execution end time")
    private LocalDateTime endTime;

    @Schema(description = "Job status, see JobLogStatusEnum")
    private Integer status;

}
