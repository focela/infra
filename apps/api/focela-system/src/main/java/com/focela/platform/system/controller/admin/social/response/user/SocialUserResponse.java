package com.focela.platform.system.controller.admin.social.response.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "Admin - social user Response")
@Data
public class SocialUserResponse {

    @Schema(description = "primary key (auto increment)", requiredMode = Schema.RequiredMode.REQUIRED, example = "14569")
    private Long id;

    @Schema(description = "Social platform type", requiredMode = Schema.RequiredMode.REQUIRED, example = "30")
    private Integer type;

    @Schema(description = "Social openid", requiredMode = Schema.RequiredMode.REQUIRED, example = "666")
    private String openid;

    @Schema(description = "social token", requiredMode = Schema.RequiredMode.REQUIRED, example = "666")
    private String token;

    @Schema(description = "raw token data, usually JSON format", requiredMode = Schema.RequiredMode.REQUIRED, example = "{}")
    private String rawTokenInfo;

    @Schema(description = "Nickname", requiredMode = Schema.RequiredMode.REQUIRED, example = "Alice")
    private String nickname;

    @Schema(description = "Avatar", example = "https://www.example.com/xxx.png")
    private String avatar;

    @Schema(description = "raw user data, usually JSON format", requiredMode = Schema.RequiredMode.REQUIRED, example = "{}")
    private String rawUserInfo;

    @Schema(description = "last auth code", requiredMode = Schema.RequiredMode.REQUIRED, example = "666666")
    private String code;

    @Schema(description = "last auth state", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    private String state;

    @Schema(description = "Created time", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "updated time", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime updateTime;

}
