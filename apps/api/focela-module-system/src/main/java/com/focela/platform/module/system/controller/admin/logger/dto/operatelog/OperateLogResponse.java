package com.focela.platform.module.system.controller.admin.logger.dto.operatelog;

import com.focela.platform.framework.excel.core.annotations.DictionaryFormat;
import com.focela.platform.module.system.repository.entity.user.AdminUserEntity;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.focela.platform.module.system.enums.DictionaryTypeConstants;
import com.fhs.core.trans.anno.Trans;
import com.fhs.core.trans.constant.TransType;
import com.fhs.core.trans.vo.VO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "Admin - operation log Response VO")
@Data
@ExcelIgnoreUnannotated
public class OperateLogResponse implements VO {

    @Schema(description = "Log ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("日志编号")
    private Long id;

    @Schema(description = "Trace ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "89aca178-a370-411c-ae02-3f0d672be4ab")
    private String traceId;

    @Schema(description = "User ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @Trans(type = TransType.SIMPLE, target = AdminUserEntity.class, fields = "nickname", ref = "userName")
    private Long userId;
    @Schema(description = "Nickname", requiredMode = Schema.RequiredMode.REQUIRED, example = "Alice")
    @ExcelProperty("操作人")
    private String userName;

    @Schema(description = "User type", requiredMode = Schema.RequiredMode.REQUIRED, example = "1", implementation = Integer.class)
    @ExcelProperty("用户类型")
    @DictionaryFormat(DictionaryTypeConstants.USER_TYPE)
    private Integer userType;

    @Schema(description = "operation module type", requiredMode = Schema.RequiredMode.REQUIRED, example = "order")
    @ExcelProperty("操作模块类型")
    private String type;

    @Schema(description = "Operation name", requiredMode = Schema.RequiredMode.REQUIRED, example = "create order")
    @ExcelProperty("操作名")
    private String subType;

    @Schema(description = "Business ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("操作模块业务编号")
    private Long bizId;

    @Schema(description = "operation detail", example = "update ID as 1 user info, gender slave male to female, name slave Acme to source.")
    private String action;

    @Schema(description = "extra field", example = "{'orderId': 1}")
    private String extra;

    @Schema(description = "HTTP method", requiredMode = Schema.RequiredMode.REQUIRED, example = "GET")
    @NotEmpty(message = "请求方法名不能为空")
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
