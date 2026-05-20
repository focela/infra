package com.focela.platform.system.controller.admin.logger.response.operation;

import com.focela.platform.excel.core.annotations.DictionaryFormat;
import com.focela.platform.system.domain.entity.user.UserEntity;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.focela.platform.system.constants.DictionaryTypeConstants;
import com.fhs.core.trans.anno.Trans;
import com.fhs.core.trans.constant.TransType;
import com.fhs.core.trans.vo.VO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "Admin - operation log Response")
@Data
@ExcelIgnoreUnannotated
public class OperateLogResponse implements VO {

    @Schema(description = "Log ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("Log ID")
    private Long id;

    @Schema(description = "Trace ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "89aca178-a370-411c-ae02-3f0d672be4ab")
    private String traceId;

    @Schema(description = "User ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @Trans(type = TransType.SIMPLE, target = UserEntity.class, fields = "nickname", ref = "userName")
    private Long userId;
    @Schema(description = "Nickname", requiredMode = Schema.RequiredMode.REQUIRED, example = "Alice")
    @ExcelProperty("Operator")
    private String userName;

    @Schema(description = "User type", requiredMode = Schema.RequiredMode.REQUIRED, example = "1", implementation = Integer.class)
    @ExcelProperty("User Type")
    @DictionaryFormat(DictionaryTypeConstants.USER_TYPE)
    private Integer userType;

    @Schema(description = "operation module type", requiredMode = Schema.RequiredMode.REQUIRED, example = "order")
    @ExcelProperty("Operation Module Type")
    private String type;

    @Schema(description = "Operation name", requiredMode = Schema.RequiredMode.REQUIRED, example = "create order")
    @ExcelProperty("Operation Name")
    private String subType;

    @Schema(description = "Business ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("Operation Module Business ID")
    private Long bizId;

    @Schema(description = "operation detail", example = "update ID as 1 user info, gender slave male to female, name slave Acme to source.")
    private String action;

    @Schema(description = "extra field", example = "{'orderId': 1}")
    private String extra;

    @Schema(description = "HTTP method", requiredMode = Schema.RequiredMode.REQUIRED, example = "GET")
    @NotEmpty(message = "HTTP method must not be blank")
    private String requestMethod;

    @Schema(description = "Request URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "/xxx/yyy")
    private String requestUrl;

    @Schema(description = "User IP", requiredMode = Schema.RequiredMode.REQUIRED, example = "127.0.0.1")
    private String userIp;

    @Schema(description = "User agent", requiredMode = Schema.RequiredMode.REQUIRED, example = "Mozilla/5.0")
    private String userAgent;

    @Schema(description = "Created time", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
