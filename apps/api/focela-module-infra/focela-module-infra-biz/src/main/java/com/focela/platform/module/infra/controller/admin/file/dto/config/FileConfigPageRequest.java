package com.focela.platform.module.infra.controller.admin.file.dto.config;

import com.focela.platform.framework.common.model.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.focela.platform.framework.common.utils.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "Admin - file config page Request VO")
@Data
public class FileConfigPageRequest extends PageParam {

    @Schema(description = "Config name", example = "S3 - Aliyun")
    private String name;

    @Schema(description = "storage", example = "1")
    private Integer storage;

    @Schema(description = "Created time", example = "[2022-07-01 00:00:00, 2022-07-01 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}