package com.focela.platform.system.controller.admin.sms.response.template;

import com.focela.platform.excel.core.annotations.DictionaryFormat;
import com.focela.platform.excel.core.converter.DictionaryConverter;
import com.focela.platform.system.constants.DictionaryTypeConstants;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Admin - SMS template Response")
@Data
@ExcelIgnoreUnannotated
public class SmsTemplateResponse {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "SMS template type, see SmsTemplateTypeEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "SMS signature", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.SMS_TEMPLATE_TYPE)
    private Integer type;

    @Schema(description = "Enable status, see CommonStatusEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "Enable status", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.COMMON_STATUS)
    private Integer status;

    @Schema(description = "Template code", requiredMode = Schema.RequiredMode.REQUIRED, example = "test_01")
    @ExcelProperty("Template code")
    private String code;

    @Schema(description = "Template name", requiredMode = Schema.RequiredMode.REQUIRED, example = "focela")
    @ExcelProperty("Template name")
    private String name;

    @Schema(description = "Template content", requiredMode = Schema.RequiredMode.REQUIRED, example = "hello, {name}. you tall too {like}!")
    @ExcelProperty("Template content")
    private String content;

    @Schema(description = "Param array", example = "name,code")
    private List<String> params;

    @Schema(description = "Remarks", example = "")
    @ExcelProperty("Remarks")
    private String remark;

    @Schema(description = "SMS API template ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "4383920")
    @ExcelProperty("SMS API template ID")
    private String apiTemplateId;

    @Schema(description = "SMS channel ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @ExcelProperty("SMS channel ID")
    private Long channelId;

    @Schema(description = "SMS channel code", requiredMode = Schema.RequiredMode.REQUIRED, example = "ALIYUN")
    @ExcelProperty(value = "SMS channel code", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.SMS_CHANNEL_CODE)
    private String channelCode;

    @Schema(description = "Created time", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("Created time")
    private LocalDateTime createTime;

}
