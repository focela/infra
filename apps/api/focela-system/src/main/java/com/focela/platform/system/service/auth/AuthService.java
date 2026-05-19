package com.focela.platform.system.service.auth;

import com.focela.platform.system.controller.admin.auth.dto.AuthLoginRequest;
import com.focela.platform.system.controller.admin.auth.dto.AuthLoginResponse;
import com.focela.platform.system.controller.admin.auth.dto.AuthRegisterRequest;
import com.focela.platform.system.controller.admin.auth.dto.AuthResetPasswordRequest;
import com.focela.platform.system.controller.admin.auth.dto.AuthSmsLoginRequest;
import com.focela.platform.system.controller.admin.auth.dto.AuthSmsSendRequest;
import com.focela.platform.system.controller.admin.auth.dto.AuthSocialLoginRequest;
import com.focela.platform.system.domain.entity.user.UserEntity;

import jakarta.validation.Valid;

/**
 * Admin authentication Service interface
 *
 * Provides user login and logout capabilities.
 */
public interface AuthService {

    /**
     * Validate username + password. If successful, return the user.
     *
     * @param username username
     * @param password password
     * @return user
     */
    UserEntity authenticate(String username, String password);

    /**
     * Account login
     *
     * @param request login info
     * @return login result
     */
    AuthLoginResponse login(@Valid AuthLoginRequest request);

    /**
     * Logout based on token
     *
     * @param token token
     * @param logType logout type
     */
    void logout(String token, Integer logType);

    /**
     * Send SMS verification code
     *
     * @param request send request
     */
    void sendSmsCode(AuthSmsSendRequest request);

    /**
     * SMS login
     *
     * @param request login info
     * @return login result
     */
    AuthLoginResponse smsLogin(AuthSmsLoginRequest request);

    /**
     * Social quick login using a code (authorization code).
     *
     * @param request login info
     * @return login result
     */
    AuthLoginResponse socialLogin(@Valid AuthSocialLoginRequest request);

    /**
     * Refresh access token
     *
     * @param refreshToken refresh token
     * @return login result
     */
    AuthLoginResponse refreshToken(String refreshToken);

    /**
     * User registration
     *
     * @param createRequest registration info
     * @return registration result
     */
    AuthLoginResponse register(AuthRegisterRequest createRequest);

    /**
     * Reset password
     *
     * @param request verification code info
     */
    void resetPassword(AuthResetPasswordRequest request);

}
