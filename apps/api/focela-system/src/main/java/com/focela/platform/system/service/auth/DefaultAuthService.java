package com.focela.platform.system.service.auth;

import cn.hutool.core.util.ObjectUtil;
import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.common.utils.monitor.TracerUtils;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.common.utils.servlet.ServletUtils;
import com.focela.platform.common.utils.validation.ValidationUtils;
import com.focela.platform.datapermission.core.annotation.DataPermission;
import com.focela.platform.system.api.logger.dto.LoginLogCreateRpcRequest;
import com.focela.platform.system.api.sms.SmsCodeApi;
import com.focela.platform.system.api.sms.dto.code.SmsCodeUseRpcRequest;
import com.focela.platform.system.api.social.dto.SocialUserBindRpcRequest;
import com.focela.platform.system.api.social.dto.SocialUserRpcResponse;
import com.focela.platform.system.controller.admin.auth.dto.*;
import com.focela.platform.system.converter.auth.AuthConverter;
import com.focela.platform.system.entity.oauth2.OAuth2AccessTokenEntity;
import com.focela.platform.system.entity.user.UserEntity;
import com.focela.platform.system.enums.logger.LoginLogTypeEnum;
import com.focela.platform.system.enums.logger.LoginResultEnum;
import com.focela.platform.system.constants.OAuth2ClientConstants;
import com.focela.platform.system.enums.sms.SmsSceneEnum;
import com.focela.platform.system.service.logger.LoginLogService;
import com.focela.platform.system.service.member.MemberService;
import com.focela.platform.system.service.oauth2.OAuth2TokenService;
import com.focela.platform.system.service.social.SocialUserService;
import com.focela.platform.system.service.user.UserService;
import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import jakarta.validation.Validator;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.common.utils.servlet.ServletUtils.getClientIP;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.*;

/**
 * Auth Service implementation class
 */
@Service
@Slf4j
public class DefaultAuthService implements AuthService {

    @Resource
    private UserService userService;
    @Resource
    private LoginLogService loginLogService;
    @Resource
    private OAuth2TokenService oauth2TokenService;
    @Resource
    private SocialUserService socialUserService;
    @Resource
    private MemberService memberService;
    @Resource
    private Validator validator;
    @Resource
    private CaptchaService captchaService;
    @Resource
    private SmsCodeApi smsCodeApi;

    /**
     * Captcha switch, defaults to true
     */
    @Value("${focela.captcha.enable:true}")
    @Setter // for unit tests: enable or disable captcha
    private Boolean captchaEnable;

    @Override
    public UserEntity authenticate(String username, String password) {
        final LoginLogTypeEnum logTypeEnum = LoginLogTypeEnum.LOGIN_USERNAME;
        // Validate whether the account exists
        UserEntity user = userService.getUserByUsername(username);
        if (user == null) {
            createLoginLog(null, username, logTypeEnum, LoginResultEnum.BAD_CREDENTIALS);
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS);
        }
        if (!userService.isPasswordMatch(password, user.getPassword())) {
            createLoginLog(user.getId(), username, logTypeEnum, LoginResultEnum.BAD_CREDENTIALS);
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS);
        }
        // Validate whether the user is disabled
        if (CommonStatusEnum.isDisable(user.getStatus())) {
            createLoginLog(user.getId(), username, logTypeEnum, LoginResultEnum.USER_DISABLED);
            throw exception(AUTH_LOGIN_USER_DISABLED);
        }
        return user;
    }

    @Override
    @DataPermission(enable = false)
    public AuthLoginResponse login(AuthLoginRequest request) {
        // Validate captcha
        validateCaptcha(request);

        // Log in with account and password
        UserEntity user = authenticate(request.getUsername(), request.getPassword());

        // If socialType is not null, it means the social user needs to be bound
        if (request.getSocialType() != null) {
            socialUserService.bindSocialUser(new SocialUserBindRpcRequest(user.getId(), getUserType().getValue(),
                    request.getSocialType(), request.getSocialCode(), request.getSocialState()));
        }
        // Create token and record login log
        return createTokenAfterLoginSuccess(user.getId(), request.getUsername(), LoginLogTypeEnum.LOGIN_USERNAME);
    }

    @Override
    public void sendSmsCode(AuthSmsSendRequest request) {
        // For reset-password scenario, verify the image captcha is correct
        if (Objects.equals(SmsSceneEnum.ADMIN_MEMBER_RESET_PASSWORD.getScene(), request.getScene())) {
            ResponseModel response = doValidateCaptcha(request);
            if (!response.isSuccess()) {
                throw exception(AUTH_REGISTER_CAPTCHA_CODE_ERROR, response.getRepMsg());
            }
        }

        // For login scenario, verify that the user exists
        if (userService.getUserByMobile(request.getMobile()) == null) {
            throw exception(AUTH_MOBILE_NOT_EXISTS);
        }
        // Send verification code
        smsCodeApi.sendSmsCode(AuthConverter.INSTANCE.convert(request).setCreateIp(getClientIP()));
    }

    @Override
    public AuthLoginResponse smsLogin(AuthSmsLoginRequest request) {
        // Validate verification code
        smsCodeApi.useSmsCode(AuthConverter.INSTANCE.convert(request, SmsSceneEnum.ADMIN_MEMBER_LOGIN.getScene(), getClientIP()));

        // Get user info
        UserEntity user = userService.getUserByMobile(request.getMobile());
        if (user == null) {
            throw exception(USER_NOT_EXISTS);
        }

        // Create token and record login log
        return createTokenAfterLoginSuccess(user.getId(), request.getMobile(), LoginLogTypeEnum.LOGIN_MOBILE);
    }

    private void createLoginLog(Long userId, String username,
                                LoginLogTypeEnum logTypeEnum, LoginResultEnum loginResult) {
        // Insert login log
        LoginLogCreateRpcRequest request = new LoginLogCreateRpcRequest();
        request.setLogType(logTypeEnum.getType());
        request.setTraceId(TracerUtils.getTraceId());
        request.setUserId(userId);
        request.setUserType(getUserType().getValue());
        request.setUsername(username);
        request.setUserAgent(ServletUtils.getUserAgent());
        request.setUserIp(ServletUtils.getClientIP());
        request.setResult(loginResult.getResult());
        loginLogService.createLoginLog(request);
        // Update last login time
        if (userId != null && Objects.equals(LoginResultEnum.SUCCESS.getResult(), loginResult.getResult())) {
            userService.updateUserLogin(userId, ServletUtils.getClientIP());
        }
    }

    @Override
    public AuthLoginResponse socialLogin(AuthSocialLoginRequest request) {
        // Use the authorization code to log in, then obtain the bound user ID
        SocialUserRpcResponse socialUser = socialUserService.getSocialUserByCode(UserTypeEnum.ADMIN.getValue(), request.getType(),
                request.getCode(), request.getState());
        if (socialUser == null || socialUser.getUserId() == null) {
            throw exception(AUTH_THIRD_LOGIN_NOT_BIND);
        }

        // Get user
        UserEntity user = userService.getUser(socialUser.getUserId());
        if (user == null) {
            throw exception(USER_NOT_EXISTS);
        }

        // Create token and record login log
        return createTokenAfterLoginSuccess(user.getId(), user.getUsername(), LoginLogTypeEnum.LOGIN_SOCIAL);
    }

    @VisibleForTesting
    void validateCaptcha(AuthLoginRequest request) {
        ResponseModel response = doValidateCaptcha(request);
        // Validate captcha
        if (!response.isSuccess()) {
            // Create login-failed log (captcha incorrect)
            createLoginLog(null, request.getUsername(), LoginLogTypeEnum.LOGIN_USERNAME, LoginResultEnum.CAPTCHA_CODE_ERROR);
            throw exception(AUTH_LOGIN_CAPTCHA_CODE_ERROR, response.getRepMsg());
        }
    }

    private ResponseModel doValidateCaptcha(CaptchaVerificationRequest request) {
        // If captcha is disabled, skip validation
        if (!captchaEnable) {
            return ResponseModel.success();
        }
        ValidationUtils.validate(validator, request, CaptchaVerificationRequest.CodeEnableGroup.class);
        CaptchaVO captchaVO = new CaptchaVO();
        captchaVO.setCaptchaVerification(request.getCaptchaVerification());
        return captchaService.verification(captchaVO);
    }

    private AuthLoginResponse createTokenAfterLoginSuccess(Long userId, String username, LoginLogTypeEnum logType) {
        // Insert login log
        createLoginLog(userId, username, logType, LoginResultEnum.SUCCESS);
        // Create access token
        OAuth2AccessTokenEntity accessTokenDO = oauth2TokenService.createAccessToken(userId, getUserType().getValue(),
                OAuth2ClientConstants.CLIENT_ID_DEFAULT, null);
        // Build return result
        return BeanUtils.toBean(accessTokenDO, AuthLoginResponse.class);
    }

    @Override
    public AuthLoginResponse refreshToken(String refreshToken) {
        OAuth2AccessTokenEntity accessTokenDO = oauth2TokenService.refreshAccessToken(refreshToken, OAuth2ClientConstants.CLIENT_ID_DEFAULT);
        return BeanUtils.toBean(accessTokenDO, AuthLoginResponse.class);
    }

    @Override
    public void logout(String token, Integer logType) {
        // Delete access token
        OAuth2AccessTokenEntity accessTokenDO = oauth2TokenService.removeAccessToken(token);
        if (accessTokenDO == null) {
            return;
        }
        // On successful deletion, record the logout log
        createLogoutLog(accessTokenDO.getUserId(), accessTokenDO.getUserType(), logType);
    }

    private void createLogoutLog(Long userId, Integer userType, Integer logType) {
        LoginLogCreateRpcRequest request = new LoginLogCreateRpcRequest();
        request.setLogType(logType);
        request.setTraceId(TracerUtils.getTraceId());
        request.setUserId(userId);
        request.setUserType(userType);
        if (ObjectUtil.equal(getUserType().getValue(), userType)) {
            request.setUsername(getUsername(userId));
        } else {
            request.setUsername(memberService.getMemberUserMobile(userId));
        }
        request.setUserAgent(ServletUtils.getUserAgent());
        request.setUserIp(ServletUtils.getClientIP());
        request.setResult(LoginResultEnum.SUCCESS.getResult());
        loginLogService.createLoginLog(request);
    }

    private String getUsername(Long userId) {
        if (userId == null) {
            return null;
        }
        UserEntity user = userService.getUser(userId);
        return user != null ? user.getUsername() : null;
    }

    private UserTypeEnum getUserType() {
        return UserTypeEnum.ADMIN;
    }

    @Override
    public AuthLoginResponse register(AuthRegisterRequest registerRequest) {
        // 1. Validate captcha
        validateCaptcha(registerRequest);

        // 2. Validate whether the username already exists
        Long userId = userService.registerUser(registerRequest);

        // 3. Create token and record login log
        return createTokenAfterLoginSuccess(userId, registerRequest.getUsername(), LoginLogTypeEnum.LOGIN_USERNAME);
    }

    @VisibleForTesting
    void validateCaptcha(AuthRegisterRequest request) {
        ResponseModel response = doValidateCaptcha(request);
        // Validation failed
        if (!response.isSuccess()) {
            throw exception(AUTH_REGISTER_CAPTCHA_CODE_ERROR, response.getRepMsg());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(AuthResetPasswordRequest request) {
        UserEntity userByMobile = userService.getUserByMobile(request.getMobile());
        if (userByMobile == null) {
            throw exception(USER_MOBILE_NOT_EXISTS);
        }

        smsCodeApi.useSmsCode(new SmsCodeUseRpcRequest()
                .setCode(request.getCode())
                .setMobile(request.getMobile())
                .setScene(SmsSceneEnum.ADMIN_MEMBER_RESET_PASSWORD.getScene())
                .setUsedIp(getClientIP())
        );

        userService.updateUserPassword(userByMobile.getId(), request.getPassword());
    }
}
