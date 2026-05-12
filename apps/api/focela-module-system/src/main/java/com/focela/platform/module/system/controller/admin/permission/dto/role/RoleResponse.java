package com.focela.platform.module.system.controller.admin.permission.dto.role;

import com.focela.platform.framework.excel.core.annotations.DictionaryFormat;
import com.focela.platform.framework.excel.core.converter.DictionaryConverter;
import com.focela.platform.module.system.enums.DictionaryTypeConstants;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "Admin - role info Response VO")
@Data
@ExcelIgnoreUnannotated
public class RoleResponse {

    @Schema(description = "Role ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("角色序号")
    private Long id;

    @Schema(description = "Role name", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin")
    @ExcelProperty("角色名称")
    private String name;

    @Schema(description = "Role code", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin")
    @NotBlank(message = "role code must not be blank")
    @ExcelProperty("角色标志")
    private String code;

    @Schema(description = "Display order", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("角色排序")
    private Integer sort;

    @Schema(description = "Status, see CommonStatusEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "角色状态", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.COMMON_STATUS)
    private Integer status;

    @Schema(description = "role type, see RoleTypeEnum enum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer type;

    @Schema(description = "Remarks", example = "I am a role")
    private String remark;

    @Schema(description = "Data scope, see DataScopeEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "数据范围", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.DATA_SCOPE)
    private Integer dataScope;

    @Schema(description = "data scope (specific department array)", example = "1")
    private Set<Long> dataScopeDeptIds;

    @Schema(description = "Created time", requiredMode = Schema.RequiredMode.REQUIRED, example = "timestamp format")
    private LocalDateTime createTime;

}
