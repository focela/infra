package com.focela.platform.module.system.controller.admin.oauth2;

import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.module.system.controller.admin.oauth2.dto.token.OAuth2AccessTokenPageRequest;
import com.focela.platform.module.system.controller.admin.oauth2.dto.token.OAuth2AccessTokenResponse;
import com.focela.platform.module.system.entity.oauth2.OAuth2AccessTokenEntity;
import com.focela.platform.module.system.enums.logger.LoginLogTypeEnum;
import com.focela.platform.module.system.service.auth.AdminAuthService;
import com.focela.platform.module.system.service.oauth2.OAuth2TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.focela.platform.framework.common.model.CommonResult.success;

@Tag(name = "Admin - OAuth2.0 token")
@RestController
@RequestMapping("/system/oauth2-token")
public class OAuth2TokenController {

    @Resource
    private OAuth2TokenService oauth2TokenService;
    @Resource
    private AdminAuthService authService;

    @GetMapping("/page")
    @Operation(summary = "get access token page", description = "return only TTL within")
    @PreAuthorize("@ss.hasPermission('system:oauth2-token:page')")
    public CommonResult<PageResult<OAuth2AccessTokenResponse>> getAccessTokenPage(@Valid OAuth2AccessTokenPageRequest request) {
        PageResult<OAuth2AccessTokenEntity> pageResult = oauth2TokenService.getAccessTokenPage(request);
        return success(BeanUtils.toBean(pageResult, OAuth2AccessTokenResponse.class));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Delete access token")
    @Parameter(name = "accessToken", description = "Access token", required = true, example = "tudou")
    @PreAuthorize("@ss.hasPermission('system:oauth2-token:delete')")
    public CommonResult<Boolean> deleteAccessToken(@RequestParam("accessToken") String accessToken) {
        authService.logout(accessToken, LoginLogTypeEnum.LOGOUT_DELETE.getType());
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "batch delete access token")
    @Parameter(name = "accessTokens", description = "access token array", required = true)
    @PreAuthorize("@ss.hasPermission('system:oauth2-token:delete')")
    public CommonResult<Boolean> deleteAccessTokenList(@RequestParam("accessTokens") List<String> accessTokens) {
        accessTokens.forEach(accessToken ->
                authService.logout(accessToken, LoginLogTypeEnum.LOGOUT_DELETE.getType()));
        return success(true);
    }

}
