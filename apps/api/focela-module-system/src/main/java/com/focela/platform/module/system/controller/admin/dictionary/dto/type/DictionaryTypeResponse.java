package com.focela.platform.module.system.controller.admin.dictionary.dto.type;

import com.focela.platform.framework.excel.core.annotations.DictionaryFormat;
import com.focela.platform.framework.excel.core.converter.DictionaryConverter;
import com.focela.platform.module.system.enums.DictionaryTypeConstants;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "Admin - dictionary type info Response VO")
@Data
@ExcelIgnoreUnannotated
public class DictionaryTypeResponse {

    @Schema(description = "Dictionary type ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("字典主键")
    private Long id;

    @Schema(description = "Dictionary name", requiredMode = Schema.RequiredMode.REQUIRED, example = "gender")
    @ExcelProperty("字典名称")
    private String name;

    @Schema(description = "Dictionary type", requiredMode = Schema.RequiredMode.REQUIRED, example = "sys_common_sex")
    @ExcelProperty("字典类型")
    private String type;

    @Schema(description = "Status, see CommonStatusEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "状态", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.COMMON_STATUS)
    private Integer status;

    @Schema(description = "Remarks", example = "happy remarks")
    private String remark;

    @Schema(description = "Created time", requiredMode = Schema.RequiredMode.REQUIRED, example = "timestamp format")
    private LocalDateTime createTime;

}
