package com.focela.platform.system.controller.admin.oauth2;

import cn.hutool.core.collection.CollUtil;
import com.focela.platform.common.model.CommonResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.controller.admin.oauth2.dto.user.OAuth2UserInfoResponse;
import com.focela.platform.system.controller.admin.oauth2.dto.user.OAuth2UserUpdateRequest;
import com.focela.platform.system.controller.admin.user.dto.profile.UserProfileUpdateRequest;
import com.focela.platform.system.domain.entity.department.DepartmentEntity;
import com.focela.platform.system.domain.entity.department.PostEntity;
import com.focela.platform.system.domain.entity.user.UserEntity;
import com.focela.platform.system.service.department.DepartmentService;
import com.focela.platform.system.service.department.PostService;
import com.focela.platform.system.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

import static com.focela.platform.common.model.CommonResult.success;
import static com.focela.platform.security.core.utils.SecurityFrameworkUtils.getLoginUserId;

/**
 * Mainly intended for external application calls
 *
 * 1. On the getUserInfo method, add @PreAuthorize("@ss.hasScope('user.read')") to declare that scope = user.read is required
 * 2. On the updateUserInfo method, add @PreAuthorize("@ss.hasScope('user.write')") to declare that scope = user.write is required
 */
@Tag(name = "Admin - OAuth2.0 user")
@RestController
@RequestMapping("/system/oauth2/user")
@Validated
@Slf4j
@RequiredArgsConstructor
public class OAuth2UserController {

        private final UserService userService;
        private final DepartmentService deptService;
        private final PostService postService;

    @GetMapping("/get")
    @Operation(summary = "get user basic info")
    @PreAuthorize("@ss.hasScope('user.read')") //
    public CommonResult<OAuth2UserInfoResponse> getUserInfo() {
        // Get user basic info
        UserEntity user = userService.getUser(getLoginUserId());
        OAuth2UserInfoResponse resp = BeanUtils.toBean(user, OAuth2UserInfoResponse.class);
        // Get department info
        if (user.getDeptId() != null) {
            DepartmentEntity dept = deptService.getDept(user.getDeptId());
            resp.setDept(BeanUtils.toBean(dept, OAuth2UserInfoResponse.Department.class));
        }
        // Get post info
        if (CollUtil.isNotEmpty(user.getPostIds())) {
            List<PostEntity> posts = postService.getPostList(user.getPostIds());
            resp.setPosts(BeanUtils.toBean(posts, OAuth2UserInfoResponse.Post.class));
        }
        return success(resp);
    }

    @PutMapping("/update")
    @Operation(summary = "update user basic info")
    @PreAuthorize("@ss.hasScope('user.write')")
    public CommonResult<Boolean> updateUserInfo(@Valid @RequestBody OAuth2UserUpdateRequest request) {
        // Here we convert UserProfileUpdateRequest =>UserProfileUpdateRequest object to reuse the interface.
        // The reason is that UserService has no BO object of its own, so reuse can only be done this way
        userService.updateUserProfile(getLoginUserId(), BeanUtils.toBean(request, UserProfileUpdateRequest.class));
        return success(true);
    }

}
