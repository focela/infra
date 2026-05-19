package com.focela.platform.infra.controller.admin.logger.response.apierrorlog;

import com.focela.platform.excel.core.annotations.DictionaryFormat;
import com.focela.platform.excel.core.converter.DictionaryConverter;
import com.focela.platform.infra.constants.DictionaryTypeConstants;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "Admin - API error log Response")
@Data
@ExcelIgnoreUnannotated
public class ApiErrorLogResponse {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "Trace ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "66600cb6-7852-11eb-9439-0242ac130002")
    @ExcelProperty("Trace ID")
    private String traceId;

    @Schema(description = "User ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "666")
    @ExcelProperty("User ID")
    private Long userId;

    @Schema(description = "User type", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "User type", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.USER_TYPE)
    private Integer userType;

    @Schema(description = "Application name", requiredMode = Schema.RequiredMode.REQUIRED, example = "dashboard")
    @ExcelProperty("Application name")
    private String applicationName;

    @Schema(description = "HTTP method", requiredMode = Schema.RequiredMode.REQUIRED, example = "GET")
    @ExcelProperty("Request method")
    private String requestMethod;

    @Schema(description = "Request URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "/xx/yy")
    @ExcelProperty("Request URL")
    private String requestUrl;

    @Schema(description = "Request params", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("Request params")
    private String requestParams;

    @Schema(description = "User IP", requiredMode = Schema.RequiredMode.REQUIRED, example = "127.0.0.1")
    @ExcelProperty("User IP")
    private String userIp;

    @Schema(description = "User agent", requiredMode = Schema.RequiredMode.REQUIRED, example = "Mozilla/5.0")
    @ExcelProperty("User agent")
    private String userAgent;

    @Schema(description = "Exception time", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("Exception time")
    private LocalDateTime exceptionTime;

    @Schema(description = "exception name", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("Exception name")
    private String exceptionName;

    @Schema(description = "exception message", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("Exception message")
    private String exceptionMessage;

    @Schema(description = "root exception message", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("Root exception message")
    private String exceptionRootCauseMessage;

    @Schema(description = "exception stack trace", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("Exception stack trace")
    private String exceptionStackTrace;

    @Schema(description = "exception class FQN", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("Exception class FQN")
    private String exceptionClassName;

    @Schema(description = "exception class file", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("Exception class file")
    private String exceptionFileName;

    @Schema(description = "exception method", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("Exception method")
    private String exceptionMethodName;

    @Schema(description = "exception line", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("Exception line number")
    private Integer exceptionLineNumber;

    @Schema(description = "Process status", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @ExcelProperty(value = "Process status", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.API_ERROR_LOG_PROCESS_STATUS)
    private Integer processStatus;

    @Schema(description = "process time", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("Process time")
    private LocalDateTime processTime;

    @Schema(description = "process user ID", example = "233")
    @ExcelProperty("Process user ID")
    private Integer processUserId;

    @Schema(description = "Created time", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("Created time")
    private LocalDateTime createTime;

}
