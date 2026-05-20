package com.focela.platform.system.controller.admin.oauth2;

import com.focela.platform.common.model.CommonResult;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.controller.admin.oauth2.request.token.OAuth2AccessTokenPageRequest;
import com.focela.platform.system.controller.admin.oauth2.response.token.OAuth2AccessTokenResponse;
import com.focela.platform.system.domain.entity.oauth2.OAuth2AccessTokenEntity;
import com.focela.platform.system.enums.logger.LoginLogTypeEnum;
import com.focela.platform.system.service.auth.AuthService;
import com.focela.platform.system.service.oauth2.OAuth2TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.focela.platform.common.model.CommonResult.success;

@Tag(name = "Admin - OAuth2.0 token")
@RestController
@RequestMapping("/system/oauth2-token")
@RequiredArgsConstructor
public class OAuth2TokenController {

    private final OAuth2TokenService oauth2TokenService;
    private final AuthService authService;

    @GetMapping("/page")
    @Operation(summary = "get access token page", description = "return only TTL within")
    @PreAuthorize("@ss.hasPermission('system:oauth2-token:page')")
    public CommonResult<PageResult<OAuth2AccessTokenResponse>> getAccessTokenPage(@Valid OAuth2AccessTokenPageRequest request) {
        PageResult<OAuth2AccessTokenEntity> pageResult = oauth2TokenService.getAccessTokenPage(request);
        return success(BeanUtils.toBean(pageResult, OAuth2AccessTokenResponse.class));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Delete access token")
    @Parameter(name = "accessToken", description = "Access token", required = true, example = "focela_alternate")
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
