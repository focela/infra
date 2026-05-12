package com.focela.platform.module.system.controller.admin.user.dto.user;

import cn.hutool.core.util.ObjectUtil;
import com.focela.platform.framework.common.validation.Mobile;
import com.focela.platform.module.system.framework.operatelog.core.DepartmentParseFunction;
import com.focela.platform.module.system.framework.operatelog.core.PostParseFunction;
import com.focela.platform.module.system.framework.operatelog.core.SexParseFunction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mzt.logapi.starter.annotation.DiffLogField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@Schema(description = "Admin - user create /update Request VO")
@Data
public class UserSaveRequest {

    @Schema(description = "User ID", example = "1024")
    private Long id;

    @Schema(description = "Username", requiredMode = Schema.RequiredMode.REQUIRED, example = "yudao")
    @NotBlank(message = "user account must not be blank")
    @Pattern(regexp = "^[a-zA-Z0-9]{4,30}$", message = "user account consists of digit, letter consists of")
    @Size(min = 4, max = 30, message = "user account length must be 4-30 characters")
    @DiffLogField(name = "Username")
    private String username;

    @Schema(description = "Nickname", requiredMode = Schema.RequiredMode.REQUIRED, example = "Alice")
    @Size(max = 30, message = "user nickname length must not exceed 30characters")
    @DiffLogField(name = "Nickname")
    private String nickname;

    @Schema(description = "Remarks", example = "I am a user")
    @DiffLogField(name = "Remarks")
    private String remark;

    @Schema(description = "Department ID", example = "I am a user")
    @DiffLogField(name = "Department", function = DepartmentParseFunction.NAME)
    private Long deptId;

    @Schema(description = "Post ID list", example = "1")
    @DiffLogField(name = "Post", function = PostParseFunction.NAME)
    private Set<Long> postIds;

    @Schema(description = "Email", example = "user@example.com")
    @Email(message = "email format is invalid")
    @Size(max = 50, message = "email length must not exceed 50 characters")
    @DiffLogField(name = "Email")
    private String email;

    @Schema(description = "Mobile number", example = "15601691300")
    @Mobile
    @DiffLogField(name = "Mobile number")
    private String mobile;

    @Schema(description = "Gender, see SexEnum", example = "1")
    @DiffLogField(name = "user gender", function = SexParseFunction.NAME)
    private Integer sex;

    @Schema(description = "Avatar", example = "https://www.example.com/xxx.png")
    @DiffLogField(name = "Avatar")
    private String avatar;

    // ========== 仅【创建】时，需要传递的字段 ==========

    @Schema(description = "Password", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @Length(min = 4, max = 16, message = "password length must be 4-16 characters")
    private String password;

    @AssertTrue(message = "password must not be blank")
    @JsonIgnore
    public boolean isPasswordValid() {
        return id != null // 修改时，不需要传递
                || (ObjectUtil.isAllNotEmpty(password)); // 新增时，必须都传递 password
    }

}
