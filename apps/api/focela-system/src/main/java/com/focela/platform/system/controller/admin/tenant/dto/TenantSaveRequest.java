package com.focela.platform.system.controller.admin.tenant.dto;

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
    @NotNull(message = "tenant name must not be blank")
    private String name;

    @Schema(description = "Contact name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Alice")
    @NotNull(message = "contact name must not be blank")
    private String contactName;

    @Schema(description = "Contact phone", example = "15601691300")
    private String contactMobile;

    @Schema(description = "Tenant status", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "tenant status")
    private Integer status;

    @Schema(description = "Bound domains", example = "https://www.example.com")
    private List<String> websites;

    @Schema(description = "Tenant package ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "tenant package ID must not be blank")
    private Long packageId;

    @Schema(description = "Expires at", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "expires at must not be blank")
    private LocalDateTime expireTime;

    @Schema(description = "Account count", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "account count must not be blank")
    private Integer accountCount;

    // ========== 仅【创建】时，需要传递的字段 ==========

    @Schema(description = "Username", requiredMode = Schema.RequiredMode.REQUIRED, example = "yudao")
    @Pattern(regexp = "^[a-zA-Z0-9]{4,30}$", message = "user account consists of digit, letter consists of")
    @Size(min = 4, max = 30, message = "user account length must be 4-30 characters")
    private String username;

    @Schema(description = "Password", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @Length(min = 4, max = 16, message = "password length must be 4-16 characters")
    private String password;

    @AssertTrue(message = "user account, password must not be blank")
    @JsonIgnore
    public boolean isUsernameValid() {
        return id != null // 修改时，不需要传递
                || (ObjectUtil.isAllNotEmpty(username, password)); // 新增时，必须都传递 username、password
    }

}
