package com.focela.platform.module.system.controller.admin.mail.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Admin - email account create /update Request VO")
@Data
public class MailAccountSaveRequest {

    @Schema(description = "ID", example = "1024")
    private Long id;

    @Schema(description = "Email", requiredMode = Schema.RequiredMode.REQUIRED, example = "user@example.com")
    @NotNull(message = "邮箱不能为空")
    @Email(message = "必须是 Email 格式")
    private String mail;

    @Schema(description = "Username", requiredMode = Schema.RequiredMode.REQUIRED, example = "yudao")
    @NotNull(message = "用户名不能为空")
    private String username;

    @Schema(description = "Password", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @NotNull(message = "密码必填")
    private String password;

    @Schema(description = "SMTP server domain", requiredMode = Schema.RequiredMode.REQUIRED, example = "www.example.com")
    @NotNull(message = "SMTP 服务器域名不能为空")
    private String host;

    @Schema(description = "SMTP server port", requiredMode = Schema.RequiredMode.REQUIRED, example = "80")
    @NotNull(message = "SMTP 服务器端口不能为空")
    private Integer port;

    @Schema(description = "Enable SSL", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "是否开启 ssl 必填")
    private Boolean sslEnable;

    @Schema(description = "Enable STARTTLS", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "是否开启 starttls 必填")
    private Boolean starttlsEnable;

}
