package com.focela.platform.infra.controller.admin.logger.request.apiaccesslog;

import com.focela.platform.common.model.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.focela.platform.common.utils.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "Admin - API access log page Request")
@Data
public class ApiAccessLogPageRequest extends PageParam {

    @Schema(description = "User ID", example = "666")
    private Long userId;

    @Schema(description = "User type", example = "2")
    private Integer userType;

    @Schema(description = "Application name", example = "dashboard")
    private String applicationName;

    @Schema(description = "request URL, fuzzy match", example = "/xxx/yyy")
    private String requestUrl;

    @Schema(description = "Start time", example = "[2022-07-01 00:00:00, 2022-07-01 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] beginTime;

    @Schema(description = "execution duration,>=, unit: milliseconds", example = "100")
    private Integer duration;

    @Schema(description = "Result code", example = "0")
    private Integer resultCode;

}
