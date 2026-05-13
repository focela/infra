package com.focela.platform.module.system.controller.admin.sms.dto.log;

import com.focela.platform.framework.excel.core.annotations.DictionaryFormat;
import com.focela.platform.framework.excel.core.converter.DictionaryConverter;
import com.focela.platform.framework.excel.core.converter.JsonConverter;
import com.focela.platform.module.system.constants.DictionaryTypeConstants;
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
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "SMS channel ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @ExcelProperty("短信渠道编号")
    private Long channelId;

    @Schema(description = "SMS channel code", requiredMode = Schema.RequiredMode.REQUIRED, example = "ALIYUN")
    @ExcelProperty("短信渠道编码")
    private String channelCode;

    @Schema(description = "Template ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    @ExcelProperty("模板编号")
    private Long templateId;

    @Schema(description = "Template code", requiredMode = Schema.RequiredMode.REQUIRED, example = "test-01")
    @ExcelProperty("模板编码")
    private String templateCode;

    @Schema(description = "SMS type", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "短信类型", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.SMS_TEMPLATE_TYPE)
    private Integer templateType;

    @Schema(description = "SMS content", requiredMode = Schema.RequiredMode.REQUIRED, example = "hello, you CAPTCHA is 1024")
    @ExcelProperty("短信内容")
    private String templateContent;

    @Schema(description = "SMS param", requiredMode = Schema.RequiredMode.REQUIRED, example = "name,code")
    @ExcelProperty(value = "短信参数", converter = JsonConverter.class)
    private Map<String, Object> templateParams;

    @Schema(description = "SMS API template ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "SMS_207945135")
    @ExcelProperty("短信 API 的模板编号")
    private String apiTemplateId;

    @Schema(description = "Mobile number", requiredMode = Schema.RequiredMode.REQUIRED, example = "15601691300")
    @ExcelProperty("手机号")
    private String mobile;

    @Schema(description = "User ID", example = "10")
    @ExcelProperty("用户编号")
    private Long userId;

    @Schema(description = "User type", example = "1")
    @ExcelProperty(value = "用户类型", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.USER_TYPE)
    private Integer userType;

    @Schema(description = "send status", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "发送状态", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.SMS_SEND_STATUS)
    private Integer sendStatus;

    @Schema(description = "Sent time")
    @ExcelProperty("发送时间")
    private LocalDateTime sendTime;

    @Schema(description = "SMS API send result code", example = "SUCCESS")
    @ExcelProperty("短信 API 发送结果的编码")
    private String apiSendCode;

    @Schema(description = "SMS API send failure hint", example = "success")
    @ExcelProperty("短信 API 发送失败的提示")
    private String apiSendMsg;

    @Schema(description = "SMS API send return unique request ID", example = "3837C6D3-B96F-428C-BBB2-86135D4B5B99")
    @ExcelProperty("短信 API 发送返回的唯一请求 ID")
    private String apiRequestId;

    @Schema(description = "SMS API send return order number", example = "62923244790")
    @ExcelProperty("短信 API 发送返回的序号")
    private String apiSerialNo;

    @Schema(description = "receive status", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @ExcelProperty(value = "接收状态", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.SMS_RECEIVE_STATUS)
    private Integer receiveStatus;

    @Schema(description = "Received time")
    @ExcelProperty("接收时间")
    private LocalDateTime receiveTime;

    @Schema(description = "API receive result code", example = "DELIVRD")
    @ExcelProperty("API 接收结果的编码")
    private String apiReceiveCode;

    @Schema(description = "API receive result description", example = "user receive success")
    @ExcelProperty("API 接收结果的说明")
    private String apiReceiveMsg;

    @Schema(description = "Created time", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
