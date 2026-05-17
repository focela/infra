package com.focela.platform.system.controller.admin.captcha;

import cn.hutool.core.util.StrUtil;
import com.focela.platform.common.utils.servlet.ServletUtils;
import com.focela.platform.tenant.core.aop.TenantIgnore;
import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@Tag(name = "Admin - CAPTCHA")
@RestController("adminCaptchaController")
@RequestMapping("/system/captcha")
@RequiredArgsConstructor
public class CaptchaController {

        private final CaptchaService captchaService;

    @PostMapping({"/get"})
    @Operation(summary = "get CAPTCHA")
    @PermitAll
    @TenantIgnore
    public ResponseModel get(@RequestBody CaptchaVO data, HttpServletRequest request) {
        assert request.getRemoteHost() != null;
        data.setBrowserInfo(getRemoteId(request));
        return captchaService.get(data);
    }

    @PostMapping("/check")
    @Operation(summary = "validate CAPTCHA")
    @PermitAll
    @TenantIgnore
    public ResponseModel check(@RequestBody CaptchaVO data, HttpServletRequest request) {
        data.setBrowserInfo(getRemoteId(request));
        return captchaService.check(data);
    }

    public static String getRemoteId(HttpServletRequest request) {
        String ip = ServletUtils.getClientIP(request);
        String ua = request.getHeader("user-agent");
        if (StrUtil.isNotBlank(ip)) {
            return ip + ua;
        }
        return request.getRemoteAddr() + ua;
    }

}
