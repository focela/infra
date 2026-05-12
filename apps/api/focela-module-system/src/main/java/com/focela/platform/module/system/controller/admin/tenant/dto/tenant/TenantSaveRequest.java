package com.focela.platform.module.system.controller.admin.tenant.dto.tenant;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Admin - tenant create /update Request VO")
@Data
public class TenantSaveRequest {

    @Schema(description = "Tenant ID", example = "1024")
    private Long id;

    @Schema(description = "Tenant name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Acme")
    @NotNull(message = "租户名不能为空")
    private String name;

    @Schema(description = "Contact name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Alice")
    @NotNull(message = "联系人不能为空")
    private String contactName;

    @Schema(description = "Contact phone", example = "15601691300")
    private String contactMobile;

    @Schema(description = "Tenant status", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "租户状态")
    private Integer status;

    @Schema(description = "Bound domains", example = "https://www.example.com")
    private List<String> websites;

    @Schema(description = "Tenant package ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "租户套餐编号不能为空")
    private Long packageId;

    @Schema(description = "Expires at", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "过期时间不能为空")
    private LocalDateTime expireTime;

    @Schema(description = "Account count", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "账号数量不能为空")
    private Integer accountCount;

    // ========== 仅【创建】时，需要传递的字段 ==========

    @Schema(description = "Username", requiredMode = Schema.RequiredMode.REQUIRED, example = "yudao")
    @Pattern(regexp = "^[a-zA-Z0-9]{4,30}$", message = "用户账号由 数字、字母 组成")
    @Size(min = 4, max = 30, message = "用户账号长度为 4-30 个字符")
    private String username;

    @Schema(description = "Password", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @Length(min = 4, max = 16, message = "密码长度为 4-16 位")
    private String password;

    @AssertTrue(message = "用户账号、密码不能为空")
    @JsonIgnore
    public boolean isUsernameValid() {
        return id != null // 修改时，不需要传递
                || (ObjectUtil.isAllNotEmpty(username, password)); // 新增时，必须都传递 username、password
    }

}
