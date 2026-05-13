package com.focela.platform.module.system.controller.admin.logger.dto.loginlog;

import com.focela.platform.framework.excel.core.annotations.DictionaryFormat;
import com.focela.platform.framework.excel.core.converter.DictionaryConverter;
import com.focela.platform.module.system.constants.DictionaryTypeConstants;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "Admin - login log Response VO")
@Data
@ExcelIgnoreUnannotated
public class LoginLogResponse {

    @Schema(description = "Log ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("日志主键")
    private Long id;

    @Schema(description = "log type, see LoginLogTypeEnum enum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "日志类型", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.LOGIN_TYPE)
    private Integer logType;

    @Schema(description = "User ID", example = "666")
    private Long userId;

    @Schema(description = "User type, see UserTypeEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer userType;

    @Schema(description = "Trace ID", example = "89aca178-a370-411c-ae02-3f0d672be4ab")
    private String traceId;

    @Schema(description = "Username", requiredMode = Schema.RequiredMode.REQUIRED, example = "yudao")
    @ExcelProperty("用户账号")
    private String username;

    @Schema(description = "login result, see LoginResultEnum enum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "登录结果", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.LOGIN_RESULT)
    private Integer result;

    @Schema(description = "User IP", requiredMode = Schema.RequiredMode.REQUIRED, example = "127.0.0.1")
    @ExcelProperty("登录 IP")
    private String userIp;

    @Schema(description = "User agent", example = "Mozilla/5.0")
    @ExcelProperty("浏览器 UA")
    private String userAgent;

    @Schema(description = "Login time", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("登录时间")
    private LocalDateTime createTime;

}
