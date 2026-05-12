package com.focela.platform.module.system.controller.admin.mail.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "Admin - email account Response VO")
@Data
public class MailAccountResponse {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "Email", requiredMode = Schema.RequiredMode.REQUIRED, example = "user@example.com")
    private String mail;

    @Schema(description = "Username", requiredMode = Schema.RequiredMode.REQUIRED, example = "yudao")
    private String username;

    @Schema(description = "Password", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    private String password;

    @Schema(description = "SMTP server domain", requiredMode = Schema.RequiredMode.REQUIRED, example = "www.example.com")
    private String host;

    @Schema(description = "SMTP server port", requiredMode = Schema.RequiredMode.REQUIRED, example = "80")
    private Integer port;

    @Schema(description = "Enable SSL", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean sslEnable;

    @Schema(description = "Enable STARTTLS", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean starttlsEnable;

    @Schema(description = "Created time", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
