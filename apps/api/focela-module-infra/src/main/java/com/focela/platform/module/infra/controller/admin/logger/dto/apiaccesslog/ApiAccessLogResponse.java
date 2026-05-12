package com.focela.platform.module.infra.controller.admin.logger.dto.apiaccesslog;

import com.focela.platform.framework.excel.core.annotations.DictionaryFormat;
import com.focela.platform.framework.excel.core.converter.DictionaryConverter;
import com.focela.platform.module.infra.enums.DictionaryTypeConstants;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "Admin - API access log Response VO")
@Data
@ExcelIgnoreUnannotated
public class ApiAccessLogResponse {

    @Schema(description = "log primary key", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("日志主键")
    private Long id;

    @Schema(description = "Trace ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "66600cb6-7852-11eb-9439-0242ac130002")
    @ExcelProperty("链路追踪编号")
    private String traceId;

    @Schema(description = "User ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "666")
    @ExcelProperty("用户编号")
    private Long userId;

    @Schema(description = "User type, see UserTypeEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "用户类型", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.USER_TYPE)
    private Integer userType;

    @Schema(description = "Application name", requiredMode = Schema.RequiredMode.REQUIRED, example = "dashboard")
    @ExcelProperty("应用名")
    private String applicationName;

    @Schema(description = "HTTP method", requiredMode = Schema.RequiredMode.REQUIRED, example = "GET")
    @ExcelProperty("请求方法名")
    private String requestMethod;

    @Schema(description = "Request URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "/xxx/yyy")
    @ExcelProperty("请求地址")
    private String requestUrl;

    @Schema(description = "Request params")
    @ExcelProperty("请求参数")
    private String requestParams;

    @Schema(description = "response result")
    @ExcelProperty("响应结果")
    private String responseBody;

    @Schema(description = "User IP", requiredMode = Schema.RequiredMode.REQUIRED, example = "127.0.0.1")
    @ExcelProperty("用户 IP")
    private String userIp;

    @Schema(description = "User agent", requiredMode = Schema.RequiredMode.REQUIRED, example = "Mozilla/5.0")
    @ExcelProperty("浏览器 UA")
    private String userAgent;

    @Schema(description = "operation module", requiredMode = Schema.RequiredMode.REQUIRED, example = "product module")
    @ExcelProperty("操作模块")
    private String operateModule;

    @Schema(description = "Operation name", requiredMode = Schema.RequiredMode.REQUIRED, example = "create product")
    @ExcelProperty("操作名")
    private String operateName;

    @Schema(description = "operation category", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "操作分类", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.OPERATE_TYPE)
    private Integer operateType;

    @Schema(description = "request start time", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("开始请求时间")
    private LocalDateTime beginTime;

    @Schema(description = "request end time", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("结束请求时间")
    private LocalDateTime endTime;

    @Schema(description = "Execution duration", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @ExcelProperty("执行时长")
    private Integer duration;

    @Schema(description = "Result code", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @ExcelProperty("结果码")
    private Integer resultCode;

    @Schema(description = "result hint", example = "Acme, awesome!")
    @ExcelProperty("结果提示")
    private String resultMsg;

    @Schema(description = "Created time", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
