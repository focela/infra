package com.focela.platform.infra.controller.admin.config.response;

import com.focela.platform.excel.core.annotations.DictionaryFormat;
import com.focela.platform.excel.core.converter.DictionaryConverter;
import com.focela.platform.infra.constants.InfraDictionaryTypeConstants;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "Admin - param config info Response")
@Data
@ExcelIgnoreUnannotated
public class ConfigResponse {

    @Schema(description = "Param config order", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("Param config ID")
    private Long id;

    @Schema(description = "param category", requiredMode = Schema.RequiredMode.REQUIRED, example = "biz")
    @ExcelProperty("Param category")
    private String category;

    @Schema(description = "Param name", requiredMode = Schema.RequiredMode.REQUIRED, example = "database name")
    @ExcelProperty("Param name")
    private String name;

    @Schema(description = "Param key", requiredMode = Schema.RequiredMode.REQUIRED, example = "focela.db.username")
    @ExcelProperty("Param key")
    private String key;

    @Schema(description = "Param value", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("Param value")
    private String value;

    @Schema(description = "Param type, see ConfigTypeEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "Param type", converter = DictionaryConverter.class)
    @DictionaryFormat(InfraDictionaryTypeConstants.CONFIG_TYPE)
    private Integer type;

    @Schema(description = "Visible", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @ExcelProperty(value = "Visible", converter = DictionaryConverter.class)
    @DictionaryFormat(InfraDictionaryTypeConstants.BOOLEAN_STRING)
    private Boolean visible;

    @Schema(description = "Remarks", example = "remarks one next very cool!")
    @ExcelProperty("Remarks")
    private String remark;

    @Schema(description = "Created time", requiredMode = Schema.RequiredMode.REQUIRED, example = "timestamp format")
    @ExcelProperty("Created time")
    private LocalDateTime createTime;

}
