package com.focela.platform.module.system.controller.admin.oauth2;

import cn.hutool.core.collection.CollUtil;
import com.focela.platform.framework.common.pojo.CommonResult;
import com.focela.platform.framework.common.util.object.BeanUtils;
import com.focela.platform.module.system.controller.admin.oauth2.dto.user.OAuth2UserInfoResponse;
import com.focela.platform.module.system.controller.admin.oauth2.dto.user.OAuth2UserUpdateRequest;
import com.focela.platform.module.system.controller.admin.user.dto.profile.UserProfileUpdateRequest;
import com.focela.platform.module.system.repository.entity.dept.DeptEntity;
import com.focela.platform.module.system.repository.entity.dept.PostEntity;
import com.focela.platform.module.system.repository.entity.user.AdminUserEntity;
import com.focela.platform.module.system.service.dept.DeptService;
import com.focela.platform.module.system.service.dept.PostService;
import com.focela.platform.module.system.service.user.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;

import static com.focela.platform.framework.common.pojo.CommonResult.success;
import static com.focela.platform.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * 提供给外部应用调用为主
 *
 * 1. 在 getUserInfo 方法上，添加 @PreAuthorize("@ss.hasScope('user.read')") 注解，声明需要满足 scope = user.read
 * 2. 在 updateUserInfo 方法上，添加 @PreAuthorize("@ss.hasScope('user.write')") 注解，声明需要满足 scope = user.write
 *
 * @author 芋道源码
 */
@Tag(name = "管理后台 - OAuth2.0 用户")
@RestController
@RequestMapping("/system/oauth2/user")
@Validated
@Slf4j
public class OAuth2UserController {

    @Resource
    private AdminUserService userService;
    @Resource
    private DeptService deptService;
    @Resource
    private PostService postService;

    @GetMapping("/get")
    @Operation(summary = "获得用户基本信息")
    @PreAuthorize("@ss.hasScope('user.read')") //
    public CommonResult<OAuth2UserInfoResponse> getUserInfo() {
        // 获得用户基本信息
        AdminUserEntity user = userService.getUser(getLoginUserId());
        OAuth2UserInfoResponse resp = BeanUtils.toBean(user, OAuth2UserInfoResponse.class);
        // 获得部门信息
        if (user.getDeptId() != null) {
            DeptEntity dept = deptService.getDept(user.getDeptId());
            resp.setDept(BeanUtils.toBean(dept, OAuth2UserInfoResponse.Dept.class));
        }
        // 获得岗位信息
        if (CollUtil.isNotEmpty(user.getPostIds())) {
            List<PostEntity> posts = postService.getPostList(user.getPostIds());
            resp.setPosts(BeanUtils.toBean(posts, OAuth2UserInfoResponse.Post.class));
        }
        return success(resp);
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户基本信息")
    @PreAuthorize("@ss.hasScope('user.write')")
    public CommonResult<Boolean> updateUserInfo(@Valid @RequestBody OAuth2UserUpdateRequest reqVO) {
        // 这里将 UserProfileUpdateRequest =》UserProfileUpdateRequest 对象，实现接口的复用。
        // 主要是，AdminUserService 没有自己的 BO 对象，所以复用只能这么做
        userService.updateUserProfile(getLoginUserId(), BeanUtils.toBean(reqVO, UserProfileUpdateRequest.class));
        return success(true);
    }

}
