package com.focela.platform.infra.controller.admin.logger.dto.apierrorlog;

import com.focela.platform.excel.core.annotations.DictionaryFormat;
import com.focela.platform.excel.core.converter.DictionaryConverter;
import com.focela.platform.infra.constants.DictionaryTypeConstants;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "Admin - API error log Response VO")
@Data
@ExcelIgnoreUnannotated
public class ApiErrorLogResponse {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "Trace ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "66600cb6-7852-11eb-9439-0242ac130002")
    @ExcelProperty("链路追踪编号")
    private String traceId;

    @Schema(description = "User ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "666")
    @ExcelProperty("用户编号")
    private Long userId;

    @Schema(description = "User type", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "用户类型", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.USER_TYPE)
    private Integer userType;

    @Schema(description = "Application name", requiredMode = Schema.RequiredMode.REQUIRED, example = "dashboard")
    @ExcelProperty("应用名")
    private String applicationName;

    @Schema(description = "HTTP method", requiredMode = Schema.RequiredMode.REQUIRED, example = "GET")
    @ExcelProperty("请求方法名")
    private String requestMethod;

    @Schema(description = "Request URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "/xx/yy")
    @ExcelProperty("请求地址")
    private String requestUrl;

    @Schema(description = "Request params", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("请求参数")
    private String requestParams;

    @Schema(description = "User IP", requiredMode = Schema.RequiredMode.REQUIRED, example = "127.0.0.1")
    @ExcelProperty("用户 IP")
    private String userIp;

    @Schema(description = "User agent", requiredMode = Schema.RequiredMode.REQUIRED, example = "Mozilla/5.0")
    @ExcelProperty("浏览器 UA")
    private String userAgent;

    @Schema(description = "Exception time", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("异常发生时间")
    private LocalDateTime exceptionTime;

    @Schema(description = "exception name", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("异常名")
    private String exceptionName;

    @Schema(description = "exception message", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("异常导致的消息")
    private String exceptionMessage;

    @Schema(description = "root exception message", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("异常导致的根消息")
    private String exceptionRootCauseMessage;

    @Schema(description = "exception stack trace", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("异常的栈轨迹")
    private String exceptionStackTrace;

    @Schema(description = "exception class FQN", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("异常发生的类全名")
    private String exceptionClassName;

    @Schema(description = "exception class file", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("异常发生的类文件")
    private String exceptionFileName;

    @Schema(description = "exception method", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("异常发生的方法名")
    private String exceptionMethodName;

    @Schema(description = "exception line", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("异常发生的方法所在行")
    private Integer exceptionLineNumber;

    @Schema(description = "Process status", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @ExcelProperty(value = "处理状态", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.API_ERROR_LOG_PROCESS_STATUS)
    private Integer processStatus;

    @Schema(description = "process time", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("处理时间")
    private LocalDateTime processTime;

    @Schema(description = "process user ID", example = "233")
    @ExcelProperty("处理用户编号")
    private Integer processUserId;

    @Schema(description = "Created time", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
