package com.focela.platform.system.controller.admin.user;

import cn.hutool.core.collection.CollUtil;
import com.focela.platform.common.model.CommonResult;
import com.focela.platform.datapermission.core.annotation.DataPermission;
import com.focela.platform.system.controller.admin.user.dto.profile.UserProfileResponse;
import com.focela.platform.system.controller.admin.user.dto.profile.UserProfileUpdatePasswordRequest;
import com.focela.platform.system.controller.admin.user.dto.profile.UserProfileUpdateRequest;
import com.focela.platform.system.converter.user.UserConverter;
import com.focela.platform.system.entity.department.DepartmentEntity;
import com.focela.platform.system.entity.department.PostEntity;
import com.focela.platform.system.entity.permission.RoleEntity;
import com.focela.platform.system.entity.user.UserEntity;
import com.focela.platform.system.service.department.DepartmentService;
import com.focela.platform.system.service.department.PostService;
import com.focela.platform.system.service.permission.PermissionService;
import com.focela.platform.system.service.permission.RoleService;
import com.focela.platform.system.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.focela.platform.common.model.CommonResult.success;
import static com.focela.platform.security.core.utils.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "Admin - User profile")
@RestController
@RequestMapping("/system/user/profile")
@Validated
@Slf4j
public class UserProfileController {

    @Resource
    private UserService userService;
    @Resource
    private DepartmentService deptService;
    @Resource
    private PostService postService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private RoleService roleService;

    @GetMapping("/get")
    @Operation(summary = "get login user info")
    @DataPermission(enable = false) // 关闭数据权限，避免只查看自己时，查询不到部门。
    public CommonResult<UserProfileResponse> getUserProfile() {
        // 获得用户基本信息
        UserEntity user = userService.getUser(getLoginUserId());
        // 获得用户角色
        List<RoleEntity> userRoles = roleService.getRoleListFromCache(permissionService.getUserRoleIdListByUserId(user.getId()));
        // 获得部门信息
        DepartmentEntity dept = user.getDeptId() != null ? deptService.getDept(user.getDeptId()) : null;
        // 获得岗位信息
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
