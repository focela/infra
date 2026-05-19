package com.focela.platform.system.controller.admin.mail.request.account;

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
    @NotNull(message = "email must not be blank")
    @Email(message = "must be Email format")
    private String mail;

    @Schema(description = "Username", requiredMode = Schema.RequiredMode.REQUIRED, example = "focela")
    @NotNull(message = "username must not be blank")
    private String username;

    @Schema(description = "Password", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @NotNull(message = "password is required")
    private String password;

    @Schema(description = "SMTP server domain", requiredMode = Schema.RequiredMode.REQUIRED, example = "www.example.com")
    @NotNull(message = "SMTP server domain must not be blank")
    private String host;

    @Schema(description = "SMTP server port", requiredMode = Schema.RequiredMode.REQUIRED, example = "80")
    @NotNull(message = "SMTP server port must not be blank")
    private Integer port;

    @Schema(description = "Enable SSL", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "whether SSL is enabled is required")
    private Boolean sslEnable;

    @Schema(description = "Enable STARTTLS", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "whether STARTTLS is enabled is required")
    private Boolean starttlsEnable;

}
