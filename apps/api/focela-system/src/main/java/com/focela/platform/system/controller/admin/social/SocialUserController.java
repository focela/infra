package com.focela.platform.system.controller.admin.social;

import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.common.model.CommonResult;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.api.social.dto.SocialUserBindRpcRequest;
import com.focela.platform.system.controller.admin.social.dto.user.SocialUserBindRequest;
import com.focela.platform.system.controller.admin.social.dto.user.SocialUserPageRequest;
import com.focela.platform.system.controller.admin.social.dto.user.SocialUserResponse;
import com.focela.platform.system.controller.admin.social.dto.user.SocialUserUnbindRequest;
import com.focela.platform.system.entity.social.SocialUserEntity;
import com.focela.platform.system.service.social.SocialUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.focela.platform.common.model.CommonResult.success;
import static com.focela.platform.common.utils.collection.CollectionUtils.convertList;
import static com.focela.platform.security.core.utils.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "Admin - Social user")
@RestController
@RequestMapping("/system/social-user")
@Validated
@RequiredArgsConstructor
public class SocialUserController {

    private final SocialUserService socialUserService;

    @PostMapping("/bind")
    @Operation(summary = "social bind, use code authorization code")
    public CommonResult<Boolean> socialBind(@RequestBody @Valid SocialUserBindRequest request) {
        socialUserService.bindSocialUser(new SocialUserBindRpcRequest().setSocialType(request.getType())
                        .setCode(request.getCode()).setState(request.getState())
                        .setUserId(getLoginUserId()).setUserType(UserTypeEnum.ADMIN.getValue()));
        return CommonResult.success(true);
    }

    @DeleteMapping("/unbind")
    @Operation(summary = "unbind social")
    public CommonResult<Boolean> socialUnbind(@RequestBody SocialUserUnbindRequest request) {
        socialUserService.unbindSocialUser(getLoginUserId(), UserTypeEnum.ADMIN.getValue(), request.getType(), request.getOpenid());
        return CommonResult.success(true);
    }

    @GetMapping("/get-bind-list")
    @Operation(summary = "get bind social user list")
    public CommonResult<List<SocialUserResponse>> getBindSocialUserList() {
        List<SocialUserEntity> list = socialUserService.getSocialUserList(getLoginUserId(), UserTypeEnum.ADMIN.getValue());
        return success(convertList(list, socialUser -> new SocialUserResponse() // return simplified info
                .setId(socialUser.getId()).setType(socialUser.getType()).setOpenid(socialUser.getOpenid())
                .setNickname(socialUser.getNickname()).setAvatar(socialUser.getNickname())));
    }

    // ==================== Social user CRUD ====================

    @GetMapping("/get")
    @Operation(summary = "get social user")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:social-user:query')")
    public CommonResult<SocialUserResponse> getSocialUser(@RequestParam("id") Long id) {
        SocialUserEntity socialUser = socialUserService.getSocialUser(id);
        return success(BeanUtils.toBean(socialUser, SocialUserResponse.class));
    }

    @GetMapping("/page")
    @Operation(summary = "get social user page")
    @PreAuthorize("@ss.hasPermission('system:social-user:query')")
    public CommonResult<PageResult<SocialUserResponse>> getSocialUserPage(@Valid SocialUserPageRequest pageVO) {
        PageResult<SocialUserEntity> pageResult = socialUserService.getSocialUserPage(pageVO);
        return success(BeanUtils.toBean(pageResult, SocialUserResponse.class));
    }

}
