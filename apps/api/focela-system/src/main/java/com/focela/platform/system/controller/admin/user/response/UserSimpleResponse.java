package com.focela.platform.system.controller.admin.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Admin - user simplified info Response")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSimpleResponse {

    @Schema(description = "User ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "Nickname", requiredMode = Schema.RequiredMode.REQUIRED, example = "Acme")
    private String nickname;

    @Schema(description = "Department ID", example = "I am a user")
    private Long deptId;
    @Schema(description = "Department name", example = "IT dept")
    private String deptName;

}
