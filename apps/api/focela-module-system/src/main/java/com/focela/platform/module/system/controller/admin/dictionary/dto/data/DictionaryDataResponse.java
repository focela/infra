package com.focela.platform.module.system.controller.admin.dictionary.dto.data;

import com.focela.platform.framework.excel.core.annotations.DictionaryFormat;
import com.focela.platform.framework.excel.core.converter.DictionaryConverter;
import com.focela.platform.module.system.constants.DictionaryTypeConstants;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "Admin - dictionary data info Response VO")
@Data
@ExcelIgnoreUnannotated
public class DictionaryDataResponse {

    @Schema(description = "Dictionary data ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("Dictionary Code")
    private Long id;

    @Schema(description = "Display order", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("Dictionary Sort")
    private Integer sort;

    @Schema(description = "Dictionary label", requiredMode = Schema.RequiredMode.REQUIRED, example = "Acme")
    @ExcelProperty("Dictionary Label")
    private String label;

    @Schema(description = "Dictionary value", requiredMode = Schema.RequiredMode.REQUIRED, example = "sample")
    @ExcelProperty("Dictionary Value")
    private String value;

    @Schema(description = "Dictionary type", requiredMode = Schema.RequiredMode.REQUIRED, example = "sys_common_sex")
    @ExcelProperty("Dictionary Type")
    private String dictType;

    @Schema(description = "Status, see CommonStatusEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "Status", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.COMMON_STATUS)
    private Integer status;

    @Schema(description = "Color type (default/primary/success/info/warning/danger)", example = "default")
    private String colorType;

    @Schema(description = "CSS style", example = "btn-visible")
    private String cssClass;

    @Schema(description = "Remarks", example = "I am a role")
    private String remark;

    @Schema(description = "Created time", requiredMode = Schema.RequiredMode.REQUIRED, example = "timestamp format")
    private LocalDateTime createTime;

}
