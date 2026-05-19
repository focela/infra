package com.focela.platform.system.controller.admin.sms.response.log;

import com.focela.platform.excel.core.annotations.DictionaryFormat;
import com.focela.platform.excel.core.converter.DictionaryConverter;
import com.focela.platform.excel.core.converter.JsonConverter;
import com.focela.platform.system.constants.DictionaryTypeConstants;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Admin - SMS log Response VO")
@Data
@ExcelIgnoreUnannotated
public class SmsLogResponse {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "SMS channel ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @ExcelProperty("SMS channel ID")
    private Long channelId;

    @Schema(description = "SMS channel code", requiredMode = Schema.RequiredMode.REQUIRED, example = "ALIYUN")
    @ExcelProperty("SMS channel code")
    private String channelCode;

    @Schema(description = "Template ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    @ExcelProperty("Template ID")
    private Long templateId;

    @Schema(description = "Template code", requiredMode = Schema.RequiredMode.REQUIRED, example = "test-01")
    @ExcelProperty("Template code")
    private String templateCode;

    @Schema(description = "SMS type", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "SMS type", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.SMS_TEMPLATE_TYPE)
    private Integer templateType;

    @Schema(description = "SMS content", requiredMode = Schema.RequiredMode.REQUIRED, example = "hello, you CAPTCHA is 1024")
    @ExcelProperty("SMS content")
    private String templateContent;

    @Schema(description = "SMS param", requiredMode = Schema.RequiredMode.REQUIRED, example = "name,code")
    @ExcelProperty(value = "SMS param", converter = JsonConverter.class)
    private Map<String, Object> templateParams;

    @Schema(description = "SMS API template ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "SMS_207945135")
    @ExcelProperty("SMS API template ID")
    private String apiTemplateId;

    @Schema(description = "Mobile number", requiredMode = Schema.RequiredMode.REQUIRED, example = "15601691300")
    @ExcelProperty("Mobile number")
    private String mobile;

    @Schema(description = "User ID", example = "10")
    @ExcelProperty("User ID")
    private Long userId;

    @Schema(description = "User type", example = "1")
    @ExcelProperty(value = "User type", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.USER_TYPE)
    private Integer userType;

    @Schema(description = "send status", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "Send status", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.SMS_SEND_STATUS)
    private Integer sendStatus;

    @Schema(description = "Sent time")
    @ExcelProperty("Sent time")
    private LocalDateTime sendTime;

    @Schema(description = "SMS API send result code", example = "SUCCESS")
    @ExcelProperty("SMS API send result code")
    private String apiSendCode;

    @Schema(description = "SMS API send failure hint", example = "success")
    @ExcelProperty("SMS API send failure hint")
    private String apiSendMsg;

    @Schema(description = "SMS API send return unique request ID", example = "3837C6D3-B96F-428C-BBB2-86135D4B5B99")
    @ExcelProperty("SMS API send return unique request ID")
    private String apiRequestId;

    @Schema(description = "SMS API send return order number", example = "62923244790")
    @ExcelProperty("SMS API send return order number")
    private String apiSerialNo;

    @Schema(description = "receive status", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @ExcelProperty(value = "Receive status", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.SMS_RECEIVE_STATUS)
    private Integer receiveStatus;

    @Schema(description = "Received time")
    @ExcelProperty("Received time")
    private LocalDateTime receiveTime;

    @Schema(description = "API receive result code", example = "DELIVRD")
    @ExcelProperty("API receive result code")
    private String apiReceiveCode;

    @Schema(description = "API receive result description", example = "user receive success")
    @ExcelProperty("API receive result description")
    private String apiReceiveMsg;

    @Schema(description = "Created time", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("Created time")
    private LocalDateTime createTime;

}
