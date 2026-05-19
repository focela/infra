package com.focela.platform.system.controller.admin.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "Admin - CAPTCHA Request VO")
@Data
public class CaptchaVerificationRequest {

    // ========== image captcha related ==========
    @Schema(description = "CAPTCHA, CAPTCHA enable when, required", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "PfcH6mgr8tpXuMWFjvW6YVaqrswIuwmWI5dsVZSg7sGpWtDCUbHuDEXl3cFB1+VvCC/rAkSwK8Fad52FSuncVg==")
    @NotEmpty(message = "CAPTCHA must not be blank", groups = CodeEnableGroup.class)
    private String captchaVerification;

    /**
     * Group for enabling captcha verification
     */
    public interface CodeEnableGroup {
    }
}
