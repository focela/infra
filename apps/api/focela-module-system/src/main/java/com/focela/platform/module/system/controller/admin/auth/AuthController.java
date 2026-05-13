package com.focela.platform.module.system.controller.admin.auth;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.common.enums.UserTypeEnum;
import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.framework.datapermission.core.annotation.DataPermission;
import com.focela.platform.framework.security.config.SecurityProperties;
import com.focela.platform.framework.security.core.utils.SecurityFrameworkUtils;
import com.focela.platform.module.system.controller.admin.auth.dto.*;
import com.focela.platform.module.system.converter.auth.AuthConverter;
import com.focela.platform.module.system.repository.entity.permission.MenuEntity;
import com.focela.platform.module.system.repository.entity.permission.RoleEntity;
import com.focela.platform.module.system.repository.entity.user.AdminUserEntity;
import com.focela.platform.module.system.enums.logger.LoginLogTypeEnum;
import com.focela.platform.module.system.service.auth.AdminAuthService;
import com.focela.platform.module.system.service.permission.MenuService;
import com.focela.platform.module.system.service.permission.PermissionService;
import com.focela.platform.module.system.service.permission.RoleService;
import com.focela.platform.module.system.service.social.SocialClientService;
import com.focela.platform.module.system.service.user.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.focela.platform.framework.common.model.CommonResult.success;
import static com.focela.platform.framework.common.utils.collection.CollectionUtils.convertSet;
import static com.focela.platform.framework.security.core.utils.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "Admin - Auth")
@RestController
@RequestMapping("/system/auth")
@Validated
@Slf4j
public class AuthController {

    @Resource
    private AdminAuthService authService;
    @Resource
    private AdminUserService userService;
    @Resource
    private RoleService roleService;
    @Resource
    private MenuService menuService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private SocialClientService socialClientService;

    @Resource
    private SecurityProperties securityProperties;

    @PostMapping("/login")
    @PermitAll
    @Operation(summary = "use account/password login")
    public CommonResult<AuthLoginResponse> login(@RequestBody @Valid AuthLoginRequest request) {
        return success(authService.login(request));
    }

    @PostMapping("/logout")
    @PermitAll
    @Operation(summary = "logout system")
    public CommonResult<Boolean> logout(HttpServletRequest request) {
        String token = SecurityFrameworkUtils.obtainAuthorization(request,
                securityProperties.getTokenHeader(), securityProperties.getTokenParameter());
        if (StrUtil.isNotBlank(token)) {
            authService.logout(token, LoginLogTypeEnum.LOGOUT_SELF.getType());
        }
        return success(true);
    }

    @PostMapping("/refresh-token")
    @PermitAll
    @Operation(summary = "Refresh token")
    @Parameter(name = "refreshToken", description = "Refresh token", required = true)
    public CommonResult<AuthLoginResponse> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        return success(authService.refreshToken(refreshToken));
    }

    @GetMapping("/get-permission-info")
    @Operation(summary = "get login user permission info")
    @DataPermission(enable = false) // 忽略数据权限，避免因为过滤，导致无法查询用户。类似：https://t.zsxq.com/LHnrp
    public CommonResult<AuthPermissionInfoResponse> getPermissionInfo() {
        // 1.1 获得用户信息
        AdminUserEntity user = userService.getUser(getLoginUserId());
        if (user == null) {
            return success(null);
        }

        // 1.2 获得角色列表
        Set<Long> roleIds = permissionService.getUserRoleIdListByUserId(getLoginUserId());
        if (CollUtil.isEmpty(roleIds)) {
            return success(AuthConverter.INSTANCE.convert(user, Collections.emptyList(), Collections.emptyList()));
        }
        List<RoleEntity> roles = roleService.getRoleList(roleIds);
        roles.removeIf(role -> !CommonStatusEnum.ENABLE.getStatus().equals(role.getStatus())); // 移除禁用的角色

        // 1.3 获得菜单列表
        Set<Long> menuIds = permissionService.getRoleMenuListByRoleId(convertSet(roles, RoleEntity::getId));
        List<MenuEntity> menuList = menuService.getMenuList(menuIds);
        menuList = menuService.filterDisableMenus(menuList);

        // 2. 拼接结果返回
        return success(AuthConverter.INSTANCE.convert(user, roles, menuList));
    }

    @PostMapping("/register")
    @PermitAll
    @Operation(summary = "register user")
    public CommonResult<AuthLoginResponse> register(@RequestBody @Valid AuthRegisterRequest registerRequest) {
        return success(authService.register(registerRequest));
    }

    // ========== 短信登录相关 ==========

    @PostMapping("/sms-login")
    @PermitAll
    @Operation(summary = "use SMS CAPTCHA login")
    // 可按需开启限流：https://github.com/YunaiV/ruoyi-vue-pro/issues/851
    // @RateLimiter(time = 60, count = 6, keyResolver = ExpressionRateLimiterKeyResolver.class, keyArg = "#request.mobile")
    public CommonResult<AuthLoginResponse> smsLogin(@RequestBody @Valid AuthSmsLoginRequest request) {
        return success(authService.smsLogin(request));
    }

    @PostMapping("/send-sms-code")
    @PermitAll
    @Operation(summary = "send mobile CAPTCHA")
    public CommonResult<Boolean> sendLoginSmsCode(@RequestBody @Valid AuthSmsSendRequest request) {
        authService.sendSmsCode(request);
        return success(true);
    }

    @PostMapping("/reset-password")
    @PermitAll
    @Operation(summary = "reset password")
    public CommonResult<Boolean> resetPassword(@RequestBody @Valid AuthResetPasswordRequest request) {
        authService.resetPassword(request);
        return success(true);
    }

    // ========== 社交登录相关 ==========

    @GetMapping("/social-auth-redirect")
    @PermitAll
    @Operation(summary = "social authorize redirect")
    @Parameters({
            @Parameter(name = "type", description = "social type", required = true),
            @Parameter(name = "redirectUri", description = "callback path")
    })
    public CommonResult<String> socialLogin(@RequestParam("type") Integer type,
                                            @RequestParam("redirectUri") String redirectUri) {
        return success(socialClientService.getAuthorizeUrl(
                type, UserTypeEnum.ADMIN.getValue(), redirectUri));
    }

    @PostMapping("/social-login")
    @PermitAll
    @Operation(summary = "quick social login, use code authorization code", description = "for not logged in user, but social account bound user")
    public CommonResult<AuthLoginResponse> socialQuickLogin(@RequestBody @Valid AuthSocialLoginRequest request) {
        return success(authService.socialLogin(request));
    }

}
