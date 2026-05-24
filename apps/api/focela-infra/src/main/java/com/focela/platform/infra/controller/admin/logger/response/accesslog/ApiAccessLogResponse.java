package com.focela.platform.infra.controller.admin.logger.response.accesslog;

import com.focela.platform.excel.core.annotations.DictionaryFormat;
import com.focela.platform.excel.core.converter.DictionaryConverter;
import com.focela.platform.infra.constants.InfraDictionaryTypeConstants;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "Admin - API access log Response")
@Data
@ExcelIgnoreUnannotated
public class ApiAccessLogResponse {

    @Schema(description = "log primary key", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("Log primary key")
    private Long id;

    @Schema(description = "Trace ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "66600cb6-7852-11eb-9439-0242ac130002")
    @ExcelProperty("Trace ID")
    private String traceId;

    @Schema(description = "User ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "666")
    @ExcelProperty("User ID")
    private Long userId;

    @Schema(description = "User type, see UserTypeEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "User type", converter = DictionaryConverter.class)
    @DictionaryFormat(InfraDictionaryTypeConstants.USER_TYPE)
    private Integer userType;

    @Schema(description = "Application name", requiredMode = Schema.RequiredMode.REQUIRED, example = "dashboard")
    @ExcelProperty("Application name")
    private String applicationName;

    @Schema(description = "HTTP method", requiredMode = Schema.RequiredMode.REQUIRED, example = "GET")
    @ExcelProperty("Request method")
    private String requestMethod;

    @Schema(description = "Request URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "/xxx/yyy")
    @ExcelProperty("Request URL")
    private String requestUrl;

    @Schema(description = "Request params")
    @ExcelProperty("Request params")
    private String requestParams;

    @Schema(description = "response result")
    @ExcelProperty("Response result")
    private String responseBody;

    @Schema(description = "User IP", requiredMode = Schema.RequiredMode.REQUIRED, example = "127.0.0.1")
    @ExcelProperty("User IP")
    private String userIp;

    @Schema(description = "User agent", requiredMode = Schema.RequiredMode.REQUIRED, example = "Mozilla/5.0")
    @ExcelProperty("User agent")
    private String userAgent;

    @Schema(description = "operation module", requiredMode = Schema.RequiredMode.REQUIRED, example = "product module")
    @ExcelProperty("Operation module")
    private String operateModule;

    @Schema(description = "Operation name", requiredMode = Schema.RequiredMode.REQUIRED, example = "create product")
    @ExcelProperty("Operation name")
    private String operateName;

    @Schema(description = "operation category", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "Operation category", converter = DictionaryConverter.class)
    @DictionaryFormat(InfraDictionaryTypeConstants.OPERATE_TYPE)
    private Integer operateType;

    @Schema(description = "request start time", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("Request start time")
    private LocalDateTime beginTime;

    @Schema(description = "request end time", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("Request end time")
    private LocalDateTime endTime;

    @Schema(description = "Execution duration", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @ExcelProperty("Execution duration")
    private Integer duration;

    @Schema(description = "Result code", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @ExcelProperty("Result code")
    private Integer resultCode;

    @Schema(description = "result hint", example = "Acme, awesome!")
    @ExcelProperty("Result message")
    private String resultMsg;

    @Schema(description = "Created time", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
