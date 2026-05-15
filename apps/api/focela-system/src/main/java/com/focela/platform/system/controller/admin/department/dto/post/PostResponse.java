package com.focela.platform.system.controller.admin.department.dto.post;

import com.focela.platform.framework.excel.core.annotations.DictionaryFormat;
import com.focela.platform.framework.excel.core.converter.DictionaryConverter;
import com.focela.platform.system.constants.DictionaryTypeConstants;
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
    @ExcelProperty("Post ID")
    private Long id;

    @Schema(description = "Post name", requiredMode = Schema.RequiredMode.REQUIRED, example = "little potato")
    @ExcelProperty("Post Name")
    private String name;

    @Schema(description = "Post code", requiredMode = Schema.RequiredMode.REQUIRED, example = "yudao")
    @ExcelProperty("Post Code")
    private String code;

    @Schema(description = "Display order", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("Post Sort")
    private Integer sort;

    @Schema(description = "Status, see CommonStatusEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "Status", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.COMMON_STATUS)
    private Integer status;

    @Schema(description = "Remarks", example = "happy remarks")
    private String remark;

    @Schema(description = "Created time", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
