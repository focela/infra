package com.focela.platform.system.controller.admin.tenant.response;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.focela.platform.excel.core.annotations.DictionaryFormat;
import com.focela.platform.excel.core.converter.DictionaryConverter;
import com.focela.platform.system.constants.DictionaryTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Admin - tenant Response")
@Data
@ExcelIgnoreUnannotated
public class TenantResponse {

    @Schema(description = "Tenant ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("Tenant ID")
    private Long id;

    @Schema(description = "Tenant name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Acme")
    @ExcelProperty("Tenant name")
    private String name;

    @Schema(description = "Contact name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Alice")
    @ExcelProperty("Contact name")
    private String contactName;

    @Schema(description = "Contact phone", example = "15601691300")
    @ExcelProperty("Contact mobile")
    private String contactMobile;

    @Schema(description = "Tenant status", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "Status", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.COMMON_STATUS)
    private Integer status;

    @Schema(description = "Bound domains", example = "https://www.example.com")
    private List<String> websites;

    @Schema(description = "Tenant package ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long packageId;

    @Schema(description = "Expires at", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime expireTime;

    @Schema(description = "Account count", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Integer accountCount;

    @Schema(description = "Created time", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("Created time")
    private LocalDateTime createTime;

}
