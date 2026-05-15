package com.focela.platform.infra.controller.admin.config.dto;

import com.focela.platform.excel.core.annotations.DictionaryFormat;
import com.focela.platform.excel.core.converter.DictionaryConverter;
import com.focela.platform.infra.constants.DictionaryTypeConstants;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "Admin - param config info Response VO")
@Data
@ExcelIgnoreUnannotated
public class ConfigResponse {

    @Schema(description = "Param config order", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("参数配置序号")
    private Long id;

    @Schema(description = "param category", requiredMode = Schema.RequiredMode.REQUIRED, example = "biz")
    @ExcelProperty("参数分类")
    private String category;

    @Schema(description = "Param name", requiredMode = Schema.RequiredMode.REQUIRED, example = "database name")
    @ExcelProperty("参数名称")
    private String name;

    @Schema(description = "Param key", requiredMode = Schema.RequiredMode.REQUIRED, example = "yunai.db.username")
    @ExcelProperty("参数键名")
    private String key;

    @Schema(description = "Param value", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("参数键值")
    private String value;

    @Schema(description = "Param type, see SysConfigTypeEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "参数类型", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.CONFIG_TYPE)
    private Integer type;

    @Schema(description = "Visible", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @ExcelProperty(value = "是否可见", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.BOOLEAN_STRING)
    private Boolean visible;

    @Schema(description = "Remarks", example = "remarks one next very cool!")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "Created time", requiredMode = Schema.RequiredMode.REQUIRED, example = "timestamp format")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
