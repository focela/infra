package com.focela.platform.module.system.service.user;

import cn.hutool.core.collection.CollUtil;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.collection.CollectionUtils;
import com.focela.platform.module.system.controller.admin.auth.dto.AuthRegisterRequest;
import com.focela.platform.module.system.controller.admin.user.dto.profile.UserProfileUpdatePasswordRequest;
import com.focela.platform.module.system.controller.admin.user.dto.profile.UserProfileUpdateRequest;
import com.focela.platform.module.system.controller.admin.user.dto.UserImportExcelDto;
import com.focela.platform.module.system.controller.admin.user.dto.UserImportResponse;
import com.focela.platform.module.system.controller.admin.user.dto.UserPageRequest;
import com.focela.platform.module.system.controller.admin.user.dto.UserSaveRequest;
import com.focela.platform.module.system.entity.user.AdminUserEntity;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台用户 Service 接口
 */
public interface AdminUserService {

    /**
     * 创建用户
     *
     * @param createRequest 用户信息
     * @return 用户编号
     */
    Long createUser(@Valid UserSaveRequest createRequest);

    /**
     * 注册用户
     *
     * @param registerRequest 用户信息
     * @return 用户编号
     */
    Long registerUser(@Valid AuthRegisterRequest registerRequest);

    /**
     * 修改用户
     *
     * @param updateRequest 用户信息
     */
    void updateUser(@Valid UserSaveRequest updateRequest);

    /**
     * 更新用户的最后登陆信息
     *
     * @param id 用户编号
     * @param loginIp 登陆 IP
     */
    void updateUserLogin(Long id, String loginIp);

    /**
     * 修改用户个人信息
     *
     * @param id 用户编号
     * @param request 用户个人信息
     */
    void updateUserProfile(Long id, @Valid UserProfileUpdateRequest request);

    /**
     * 修改用户个人密码
     *
     * @param id 用户编号
     * @param request 更新用户个人密码
     */
    void updateUserPassword(Long id, @Valid UserProfileUpdatePasswordRequest request);

    /**
     * 修改密码
     *
     * @param id       用户编号
     * @param password 密码
     */
    void updateUserPassword(Long id, String password);

    /**
     * 修改状态
     *
     * @param id     用户编号
     * @param status 状态
     */
    void updateUserStatus(Long id, Integer status);

    /**
     * 删除用户
     *
     * @param id 用户编号
     */
    void deleteUser(Long id);

    /**
     * 批量删除用户
     *
     * @param ids 用户编号数组
     */
    void deleteUserList(List<Long> ids);

    /**
     * 通过用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象信息
     */
    AdminUserEntity getUserByUsername(String username);

    /**
     * 通过手机号获取用户
     *
     * @param mobile 手机号
     * @return 用户对象信息
     */
    AdminUserEntity getUserByMobile(String mobile);

    /**
     * 获得用户分页列表
     *
     * @param request 分页条件
     * @return 分页列表
     */
    PageResult<AdminUserEntity> getUserPage(UserPageRequest request);

    /**
     * 通过用户 ID 查询用户
     *
     * @param id 用户ID
     * @return 用户对象信息
     */
    AdminUserEntity getUser(Long id);

    /**
     * 获得指定部门的用户数组
     *
     * @param deptIds 部门数组
     * @return 用户数组
     */
    List<AdminUserEntity> getUserListByDeptIds(Collection<Long> deptIds);

    /**
     * 获得指定岗位的用户数组
     *
     * @param postIds 岗位数组
     * @return 用户数组
     */
    List<AdminUserEntity> getUserListByPostIds(Collection<Long> postIds);

    /**
     * 获得用户列表
     *
     * @param ids 用户编号数组
     * @return 用户列表
     */
    List<AdminUserEntity> getUserList(Collection<Long> ids);

    /**
     * 校验用户们是否有效。如下情况，视为无效：
     * 1. 用户编号不存在
     * 2. 用户被禁用
     *
     * @param ids 用户编号数组
     */
    void validateUserList(Collection<Long> ids);

    /**
     * 获得用户 Map
     *
     * @param ids 用户编号数组
     * @return 用户 Map
     */
    default Map<Long, AdminUserEntity> getUserMap(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return new HashMap<>();
        }
        return CollectionUtils.convertMap(getUserList(ids), AdminUserEntity::getId);
    }

    /**
     * 获得用户列表，基于昵称模糊匹配
     *
     * @param nickname 昵称
     * @return 用户列表
     */
    List<AdminUserEntity> getUserListByNickname(String nickname);

    /**
     * 批量导入用户
     *
     * @param importUsers     导入用户列表
     * @param isUpdateSupport 是否支持更新
     * @return 导入结果
     */
    UserImportResponse importUserList(List<UserImportExcelDto> importUsers, boolean isUpdateSupport);

    /**
     * 获得指定状态的用户们
     *
     * @param status 状态
     * @return 用户们
     */
    List<AdminUserEntity> getUserListByStatus(Integer status);

    /**
     * 判断密码是否匹配
     *
     * @param rawPassword 未加密的密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    boolean isPasswordMatch(String rawPassword, String encodedPassword);

}
