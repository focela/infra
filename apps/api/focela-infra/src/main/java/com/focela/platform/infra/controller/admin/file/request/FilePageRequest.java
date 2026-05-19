package com.focela.platform.infra.controller.admin.file.request;

import com.focela.platform.common.model.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.focela.platform.common.utils.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "Admin - file page Request")
@Data
public class FilePageRequest extends PageParam {

    @Schema(description = "file path, fuzzy match", example = "focela")
    private String path;

    @Schema(description = "file type, fuzzy match", example = "jpg")
    private String type;

    @Schema(description = "Created time", example = "[2022-07-01 00:00:00, 2022-07-01 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
