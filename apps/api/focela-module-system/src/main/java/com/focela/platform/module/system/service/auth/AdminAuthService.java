package com.focela.platform.module.system.service.auth;

import com.focela.platform.module.system.controller.admin.auth.dto.*;
import com.focela.platform.module.system.repository.entity.user.AdminUserEntity;

import jakarta.validation.Valid;

/**
 * 管理后台的认证 Service 接口
 *
 * 提供用户的登录、登出的能力
 *
 * @author 芋道源码
 */
public interface AdminAuthService {

    /**
     * 验证账号 + 密码。如果通过，则返回用户
     *
     * @param username 账号
     * @param password 密码
     * @return 用户
     */
    AdminUserEntity authenticate(String username, String password);

    /**
     * 账号登录
     *
     * @param request 登录信息
     * @return 登录结果
     */
    AuthLoginResponse login(@Valid AuthLoginRequest request);

    /**
     * 基于 token 退出登录
     *
     * @param token token
     * @param logType 登出类型
     */
    void logout(String token, Integer logType);

    /**
     * 短信验证码发送
     *
     * @param request 发送请求
     */
    void sendSmsCode(AuthSmsSendRequest request);

    /**
     * 短信登录
     *
     * @param request 登录信息
     * @return 登录结果
     */
    AuthLoginResponse smsLogin(AuthSmsLoginRequest request);

    /**
     * 社交快捷登录，使用 code 授权码
     *
     * @param request 登录信息
     * @return 登录结果
     */
    AuthLoginResponse socialLogin(@Valid AuthSocialLoginRequest request);

    /**
     * 刷新访问令牌
     *
     * @param refreshToken 刷新令牌
     * @return 登录结果
     */
    AuthLoginResponse refreshToken(String refreshToken);

    /**
     * 用户注册
     *
     * @param createRequest 注册用户
     * @return 注册结果
     */
    AuthLoginResponse register(AuthRegisterRequest createRequest);

    /**
     * 重置密码
     *
     * @param request 验证码信息
     */
    void resetPassword(AuthResetPasswordRequest request);

}
