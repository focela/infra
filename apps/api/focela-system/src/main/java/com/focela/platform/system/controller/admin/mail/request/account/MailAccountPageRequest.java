package com.focela.platform.system.controller.admin.mail.request.account;

import com.focela.platform.common.model.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "Admin - email account page Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MailAccountPageRequest extends PageParam {

    @Schema(description = "Email", requiredMode = Schema.RequiredMode.REQUIRED, example = "user@example.com")
    private String mail;

    @Schema(description = "Username" , requiredMode = Schema.RequiredMode.REQUIRED , example = "focela")
    private String username;

}
