package com.focela.platform.module.system.controller.admin.department.dto.post;

import com.focela.platform.framework.excel.core.annotations.DictionaryFormat;
import com.focela.platform.framework.excel.core.converter.DictionaryConverter;
import com.focela.platform.module.system.enums.DictionaryTypeConstants;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "Admin - post info Response VO")
@Data
@ExcelIgnoreUnannotated
public class PostResponse {

    @Schema(description = "Post order", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("岗位序号")
    private Long id;

    @Schema(description = "Post name", requiredMode = Schema.RequiredMode.REQUIRED, example = "little potato")
    @ExcelProperty("岗位名称")
    private String name;

    @Schema(description = "Post code", requiredMode = Schema.RequiredMode.REQUIRED, example = "yudao")
    @ExcelProperty("岗位编码")
    private String code;

    @Schema(description = "Display order", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("岗位排序")
    private Integer sort;

    @Schema(description = "Status, see CommonStatusEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "状态", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.COMMON_STATUS)
    private Integer status;

    @Schema(description = "Remarks", example = "happy remarks")
    private String remark;

    @Schema(description = "Created time", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
