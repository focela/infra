package com.focela.platform.module.system.controller.admin.social;

import com.focela.platform.framework.common.enums.UserTypeEnum;
import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.module.system.api.social.dto.SocialUserBindReqDTO;
import com.focela.platform.module.system.controller.admin.social.dto.user.SocialUserBindRequest;
import com.focela.platform.module.system.controller.admin.social.dto.user.SocialUserPageRequest;
import com.focela.platform.module.system.controller.admin.social.dto.user.SocialUserResponse;
import com.focela.platform.module.system.controller.admin.social.dto.user.SocialUserUnbindRequest;
import com.focela.platform.module.system.repository.entity.social.SocialUserEntity;
import com.focela.platform.module.system.service.social.SocialUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.focela.platform.framework.common.model.CommonResult.success;
import static com.focela.platform.framework.common.utils.collection.CollectionUtils.convertList;
import static com.focela.platform.framework.security.core.utils.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - 社交用户")
@RestController
@RequestMapping("/system/social-user")
@Validated
public class SocialUserController {

    @Resource
    private SocialUserService socialUserService;

    @PostMapping("/bind")
    @Operation(summary = "社交绑定，使用 code 授权码")
    public CommonResult<Boolean> socialBind(@RequestBody @Valid SocialUserBindRequest request) {
        socialUserService.bindSocialUser(new SocialUserBindReqDTO().setSocialType(request.getType())
                        .setCode(request.getCode()).setState(request.getState())
                        .setUserId(getLoginUserId()).setUserType(UserTypeEnum.ADMIN.getValue()));
        return CommonResult.success(true);
    }

    @DeleteMapping("/unbind")
    @Operation(summary = "取消社交绑定")
    public CommonResult<Boolean> socialUnbind(@RequestBody SocialUserUnbindRequest request) {
        socialUserService.unbindSocialUser(getLoginUserId(), UserTypeEnum.ADMIN.getValue(), request.getType(), request.getOpenid());
        return CommonResult.success(true);
    }

    @GetMapping("/get-bind-list")
    @Operation(summary = "获得绑定社交用户列表")
    public CommonResult<List<SocialUserResponse>> getBindSocialUserList() {
        List<SocialUserEntity> list = socialUserService.getSocialUserList(getLoginUserId(), UserTypeEnum.ADMIN.getValue());
        return success(convertList(list, socialUser -> new SocialUserResponse() // 返回精简信息
                .setId(socialUser.getId()).setType(socialUser.getType()).setOpenid(socialUser.getOpenid())
                .setNickname(socialUser.getNickname()).setAvatar(socialUser.getNickname())));
    }

    // ==================== 社交用户 CRUD ====================

    @GetMapping("/get")
    @Operation(summary = "获得社交用户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:social-user:query')")
    public CommonResult<SocialUserResponse> getSocialUser(@RequestParam("id") Long id) {
        SocialUserEntity socialUser = socialUserService.getSocialUser(id);
        return success(BeanUtils.toBean(socialUser, SocialUserResponse.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得社交用户分页")
    @PreAuthorize("@ss.hasPermission('system:social-user:query')")
    public CommonResult<PageResult<SocialUserResponse>> getSocialUserPage(@Valid SocialUserPageRequest pageVO) {
        PageResult<SocialUserEntity> pageResult = socialUserService.getSocialUserPage(pageVO);
        return success(BeanUtils.toBean(pageResult, SocialUserResponse.class));
    }

}
