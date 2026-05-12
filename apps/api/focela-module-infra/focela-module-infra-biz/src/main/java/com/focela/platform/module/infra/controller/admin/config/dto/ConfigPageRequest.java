package com.focela.platform.module.infra.controller.admin.config.dto;

import com.focela.platform.framework.common.model.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.focela.platform.framework.common.utils.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "Admin - param config page Request VO")
@Data
public class ConfigPageRequest extends PageParam {

    @Schema(description = "datasource name, fuzzy match", example = "name")
    private String name;

    @Schema(description = "param key, fuzzy match", example = "yunai.db.username")
    private String key;

    @Schema(description = "Param type, see SysConfigTypeEnum", example = "1")
    private Integer type;

    @Schema(description = "Created time", example = "[2022-07-01 00:00:00,2022-07-01 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
