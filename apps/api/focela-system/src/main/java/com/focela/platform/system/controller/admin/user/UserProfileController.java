package com.focela.platform.system.controller.admin.user;

import cn.hutool.core.collection.CollUtil;
import com.focela.platform.common.model.CommonResult;
import com.focela.platform.datapermission.core.annotation.DataPermission;
import com.focela.platform.system.controller.admin.user.response.profile.UserProfileResponse;
import com.focela.platform.system.controller.admin.user.request.profile.UserProfileUpdatePasswordRequest;
import com.focela.platform.system.controller.admin.user.request.profile.UserProfileUpdateRequest;
import com.focela.platform.system.converter.user.UserConverter;
import com.focela.platform.system.domain.entity.department.DepartmentEntity;
import com.focela.platform.system.domain.entity.department.PostEntity;
import com.focela.platform.system.domain.entity.permission.RoleEntity;
import com.focela.platform.system.domain.entity.user.UserEntity;
import com.focela.platform.system.service.department.DepartmentService;
import com.focela.platform.system.service.department.PostService;
import com.focela.platform.system.service.permission.PermissionService;
import com.focela.platform.system.service.permission.RoleService;
import com.focela.platform.system.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.focela.platform.common.model.CommonResult.success;
import static com.focela.platform.security.core.utils.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "Admin - User profile")
@RestController
@RequestMapping("/system/user/profile")
@Validated
@Slf4j
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;
    private final DepartmentService deptService;
    private final PostService postService;
    private final PermissionService permissionService;
    private final RoleService roleService;

    @GetMapping("/get")
    @Operation(summary = "get login user info")
    @DataPermission(enable = false) // disable data permission to avoid the department being unreachable when viewing only one's own info.
    public CommonResult<UserProfileResponse> getUserProfile() {
        // Get user basic info
        UserEntity user = userService.getUser(getLoginUserId());
        // Get user roles
        List<RoleEntity> userRoles = roleService.getRoleListFromCache(permissionService.getUserRoleIdListByUserId(user.getId()));
        // Get department info
        DepartmentEntity dept = user.getDeptId() != null ? deptService.getDept(user.getDeptId()) : null;
        // Get post info
        List<PostEntity> posts = CollUtil.isNotEmpty(user.getPostIds()) ? postService.getPostList(user.getPostIds()) : null;
        return success(UserConverter.INSTANCE.convert(user, userRoles, dept, posts));
    }

    @PutMapping("/update")
    @Operation(summary = "update user personal info")
    public CommonResult<Boolean> updateUserProfile(@Valid @RequestBody UserProfileUpdateRequest request) {
        userService.updateUserProfile(getLoginUserId(), request);
        return success(true);
    }

    @PutMapping("/update-password")
    @Operation(summary = "update user personal password")
    public CommonResult<Boolean> updateUserProfilePassword(@Valid @RequestBody UserProfileUpdatePasswordRequest request) {
        userService.updateUserPassword(getLoginUserId(), request);
        return success(true);
    }

}
