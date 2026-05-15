package com.focela.platform.system.controller.admin.dictionary.dto.type;

import com.focela.platform.framework.common.model.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

import static com.focela.platform.framework.common.utils.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "Admin - Dictionary type page request")
@Data
@EqualsAndHashCode(callSuper = true)
public class DictionaryTypePageRequest extends PageParam {

    @Schema(description = "dictionary type name, fuzzy match", example = "Acme")
    private String name;

    @Schema(description = "Dictionary type (fuzzy match)", example = "sys_common_sex")
    @Size(max = 100, message = "dictionary type type length must not exceed 100characters")
    private String type;

    @Schema(description = "Display status, see CommonStatusEnum", example = "1")
    private Integer status;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "Created time")
    private LocalDateTime[] createTime;

}
