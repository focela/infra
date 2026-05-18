package com.focela.platform.system.service.auth;

import cn.hutool.core.util.ReflectUtil;
import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.api.sms.SmsCodeApi;
import com.focela.platform.system.api.social.dto.SocialUserBindRpcRequest;
import com.focela.platform.system.api.social.dto.SocialUserRpcResponse;
import com.focela.platform.system.controller.admin.auth.dto.*;
import com.focela.platform.system.entity.oauth2.OAuth2AccessTokenEntity;
import com.focela.platform.system.entity.user.UserEntity;
import com.focela.platform.system.enums.logger.LoginLogTypeEnum;
import com.focela.platform.system.enums.logger.LoginResultEnum;
import com.focela.platform.system.enums.sms.SmsSceneEnum;
import com.focela.platform.system.enums.social.SocialTypeEnum;
import com.focela.platform.system.service.logger.LoginLogService;
import com.focela.platform.system.service.member.MemberService;
import com.focela.platform.system.service.oauth2.OAuth2TokenService;
import com.focela.platform.system.service.social.SocialUserService;
import com.focela.platform.system.service.user.UserService;
import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.service.CaptchaService;
import jakarta.annotation.Resource;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static cn.hutool.core.util.RandomUtil.randomEle;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.test.core.utils.RandomUtils.randomPojo;
import static com.focela.platform.test.core.utils.RandomUtils.randomString;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@Import(DefaultAuthService.class)
public class DefaultAuthServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultAuthService authService;

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private CaptchaService captchaService;
    @MockitoBean
    private LoginLogService loginLogService;
    @MockitoBean
    private SocialUserService socialUserService;
    @MockitoBean
    private SmsCodeApi smsCodeApi;
    @MockitoBean
    private OAuth2TokenService oauth2TokenService;
    @MockitoBean
    private MemberService memberService;
    @MockitoBean
    private Validator validator;

    @BeforeEach
    public void setUp() {
        authService.setCaptchaEnable(true);
        // inject a Validator object
        ReflectUtil.setFieldValue(authService, "validator",
                Validation.buildDefaultValidatorFactory().getValidator());
    }

    @Test
    public void testAuthenticate_success() {
        // prepare parameters
        String username = randomString();
        String password = randomString();
        // mock user data
        UserEntity user = randomPojo(UserEntity.class, o -> o.setUsername(username)
                .setPassword(password).setStatus(CommonStatusEnum.ENABLE.getStatus()));
        when(userService.getUserByUsername(eq(username))).thenReturn(user);
        // mock password match
        when(userService.isPasswordMatch(eq(password), eq(user.getPassword()))).thenReturn(true);

        // invoke
        UserEntity loginUser = authService.authenticate(username, password);
        // verify
        assertPojoEquals(user, loginUser);
    }

    @Test
    public void testAuthenticate_userNotFound() {
        // prepare parameters
        String username = randomString();
        String password = randomString();

        // invoke and assert exception
        assertServiceException(() -> authService.authenticate(username, password),
                AUTH_LOGIN_BAD_CREDENTIALS);
        verify(loginLogService).createLoginLog(
                argThat(o -> o.getLogType().equals(LoginLogTypeEnum.LOGIN_USERNAME.getType())
                        && o.getResult().equals(LoginResultEnum.BAD_CREDENTIALS.getResult())
                        && o.getUserId() == null)
        );
    }

    @Test
    public void testAuthenticate_badCredentials() {
        // prepare parameters
        String username = randomString();
        String password = randomString();
        // mock user data
        UserEntity user = randomPojo(UserEntity.class, o -> o.setUsername(username)
                .setPassword(password).setStatus(CommonStatusEnum.ENABLE.getStatus()));
        when(userService.getUserByUsername(eq(username))).thenReturn(user);

        // invoke and assert exception
        assertServiceException(() -> authService.authenticate(username, password),
                AUTH_LOGIN_BAD_CREDENTIALS);
        verify(loginLogService).createLoginLog(
                argThat(o -> o.getLogType().equals(LoginLogTypeEnum.LOGIN_USERNAME.getType())
                        && o.getResult().equals(LoginResultEnum.BAD_CREDENTIALS.getResult())
                        && o.getUserId().equals(user.getId()))
        );
    }

    @Test
    public void testAuthenticate_userDisabled() {
        // prepare parameters
        String username = randomString();
        String password = randomString();
        // mock user data
        UserEntity user = randomPojo(UserEntity.class, o -> o.setUsername(username)
                .setPassword(password).setStatus(CommonStatusEnum.DISABLE.getStatus()));
        when(userService.getUserByUsername(eq(username))).thenReturn(user);
        // mock password match
        when(userService.isPasswordMatch(eq(password), eq(user.getPassword()))).thenReturn(true);

        // invoke and assert exception
        assertServiceException(() -> authService.authenticate(username, password),
                AUTH_LOGIN_USER_DISABLED);
        verify(loginLogService).createLoginLog(
                argThat(o -> o.getLogType().equals(LoginLogTypeEnum.LOGIN_USERNAME.getType())
                        && o.getResult().equals(LoginResultEnum.USER_DISABLED.getResult())
                        && o.getUserId().equals(user.getId()))
        );
    }

    @Test
    public void testLogin_success() {
        // prepare parameters
        AuthLoginRequest request = randomPojo(AuthLoginRequest.class, o ->
                o.setUsername("test_username").setPassword("test_password")
                        .setSocialType(randomEle(SocialTypeEnum.values()).getType()));

        // mock verification code is correct
        authService.setCaptchaEnable(false);
        // mock user data
        UserEntity user = randomPojo(UserEntity.class, o -> o.setId(1L).setUsername("test_username")
                .setPassword("test_password").setStatus(CommonStatusEnum.ENABLE.getStatus()));
        when(userService.getUserByUsername(eq("test_username"))).thenReturn(user);
        // mock password match
        when(userService.isPasswordMatch(eq("test_password"), eq(user.getPassword()))).thenReturn(true);
        // mock cache the logged-in user to Redis
        OAuth2AccessTokenEntity accessTokenEntity = randomPojo(OAuth2AccessTokenEntity.class, o -> o.setUserId(1L)
                .setUserType(UserTypeEnum.ADMIN.getValue()));
        when(oauth2TokenService.createAccessToken(eq(1L), eq(UserTypeEnum.ADMIN.getValue()), eq("default"), isNull()))
                .thenReturn(accessTokenEntity);

        // invoke, and verify
        AuthLoginResponse loginResponse = authService.login(request);
        assertPojoEquals(accessTokenEntity, loginResponse);
        // verify call parameters
        verify(loginLogService).createLoginLog(
                argThat(o -> o.getLogType().equals(LoginLogTypeEnum.LOGIN_USERNAME.getType())
                        && o.getResult().equals(LoginResultEnum.SUCCESS.getResult())
                        && o.getUserId().equals(user.getId()))
        );
        verify(socialUserService).bindSocialUser(eq(new SocialUserBindRpcRequest(
                user.getId(), UserTypeEnum.ADMIN.getValue(),
                request.getSocialType(), request.getSocialCode(), request.getSocialState())));
    }

    @Test
    public void testSendSmsCode() {
        // prepare parameters
        String mobile = randomString();
        Integer scene = SmsSceneEnum.ADMIN_MEMBER_LOGIN.getScene();
        AuthSmsSendRequest request = new AuthSmsSendRequest(mobile, scene);
        // mock the method（user info）
        UserEntity user = randomPojo(UserEntity.class);
        when(userService.getUserByMobile(eq(mobile))).thenReturn(user);

        // invoke
        authService.sendSmsCode(request);
        // assert
        verify(smsCodeApi).sendSmsCode(argThat(sendRequest -> {
            assertEquals(mobile, sendRequest.getMobile());
            assertEquals(scene, sendRequest.getScene());
            return true;
        }));
    }

    @Test
    public void testSmsLogin_success() {
        // prepare parameters
        String mobile = randomString();
        String code = randomString();
        AuthSmsLoginRequest request = new AuthSmsLoginRequest(mobile, code);
        // mock the method（verification code）
        doNothing().when(smsCodeApi).useSmsCode((argThat(smsCodeUseRequest -> {
            assertEquals(mobile, smsCodeUseRequest.getMobile());
            assertEquals(code, smsCodeUseRequest.getCode());
            assertEquals(SmsSceneEnum.ADMIN_MEMBER_LOGIN.getScene(), smsCodeUseRequest.getScene());
            return true;
        })));
        // mock the method（user info）
        UserEntity user = randomPojo(UserEntity.class, o -> o.setId(1L));
        when(userService.getUserByMobile(eq(mobile))).thenReturn(user);
        // mock cache the logged-in user to Redis
        OAuth2AccessTokenEntity accessTokenEntity = randomPojo(OAuth2AccessTokenEntity.class, o -> o.setUserId(1L)
                .setUserType(UserTypeEnum.ADMIN.getValue()));
        when(oauth2TokenService.createAccessToken(eq(1L), eq(UserTypeEnum.ADMIN.getValue()), eq("default"), isNull()))
                .thenReturn(accessTokenEntity);

        // invoke, and assert
        AuthLoginResponse loginResponse = authService.smsLogin(request);
        assertPojoEquals(accessTokenEntity, loginResponse);
        // assert call
        verify(loginLogService).createLoginLog(
                argThat(o -> o.getLogType().equals(LoginLogTypeEnum.LOGIN_MOBILE.getType())
                        && o.getResult().equals(LoginResultEnum.SUCCESS.getResult())
                        && o.getUserId().equals(user.getId()))
        );
    }

    @Test
    public void testSocialLogin_success() {
        // prepare parameters
        AuthSocialLoginRequest request = randomPojo(AuthSocialLoginRequest.class);
        // mock the method（bound user ID）
        Long userId = 1L;
        when(socialUserService.getSocialUserByCode(eq(UserTypeEnum.ADMIN.getValue()), eq(request.getType()),
                eq(request.getCode()), eq(request.getState()))).thenReturn(new SocialUserRpcResponse(randomString(), randomString(), randomString(), userId));
        // mock（user）
        UserEntity user = randomPojo(UserEntity.class, o -> o.setId(userId));
        when(userService.getUser(eq(userId))).thenReturn(user);
        // mock cache the logged-in user to Redis
        OAuth2AccessTokenEntity accessTokenEntity = randomPojo(OAuth2AccessTokenEntity.class, o -> o.setUserId(1L)
                .setUserType(UserTypeEnum.ADMIN.getValue()));
        when(oauth2TokenService.createAccessToken(eq(1L), eq(UserTypeEnum.ADMIN.getValue()), eq("default"), isNull()))
                .thenReturn(accessTokenEntity);

        // invoke, and assert
        AuthLoginResponse loginResponse = authService.socialLogin(request);
        assertPojoEquals(accessTokenEntity, loginResponse);
        // assert call
        verify(loginLogService).createLoginLog(
                argThat(o -> o.getLogType().equals(LoginLogTypeEnum.LOGIN_SOCIAL.getType())
                        && o.getResult().equals(LoginResultEnum.SUCCESS.getResult())
                        && o.getUserId().equals(user.getId()))
        );
    }

    @Test
    public void testValidateCaptcha_successWithEnable() {
        // prepare parameters
        AuthLoginRequest request = randomPojo(AuthLoginRequest.class);

        // mock validation passed
        when(captchaService.verification(argThat(captcha -> {
            assertEquals(request.getCaptchaVerification(), captcha.getCaptchaVerification());
            return true;
        }))).thenReturn(ResponseModel.success());

        // invoke, no assertion needed
        authService.validateCaptcha(request);
    }

    @Test
    public void testValidateCaptcha_successWithDisable() {
        // prepare parameters
        AuthLoginRequest request = randomPojo(AuthLoginRequest.class);

        // mock verification code disabled
        authService.setCaptchaEnable(false);

        // invoke, no assertion needed
        authService.validateCaptcha(request);
    }

    @Test
    public void testCaptcha_fail() {
        // prepare parameters
        AuthLoginRequest request = randomPojo(AuthLoginRequest.class);

        // mock validation passed
        when(captchaService.verification(argThat(captcha -> {
            assertEquals(request.getCaptchaVerification(), captcha.getCaptchaVerification());
            return true;
        }))).thenReturn(ResponseModel.errorMsg("just wrong"));

        // invoke and assert exception
        assertServiceException(() -> authService.validateCaptcha(request), AUTH_LOGIN_CAPTCHA_CODE_ERROR, "just wrong");
        // verify call parameters
        verify(loginLogService).createLoginLog(
                argThat(o -> o.getLogType().equals(LoginLogTypeEnum.LOGIN_USERNAME.getType())
                        && o.getResult().equals(LoginResultEnum.CAPTCHA_CODE_ERROR.getResult()))
        );
    }

    @Test
    public void testRefreshToken() {
        // prepare parameters
        String refreshToken = randomString();
        // mock the method
        OAuth2AccessTokenEntity accessTokenEntity = randomPojo(OAuth2AccessTokenEntity.class);
        when(oauth2TokenService.refreshAccessToken(eq(refreshToken), eq("default")))
                .thenReturn(accessTokenEntity);

        // invoke
        AuthLoginResponse loginResponse = authService.refreshToken(refreshToken);
        // assert
        assertPojoEquals(accessTokenEntity, loginResponse);
    }

    @Test
    public void testLogout_success() {
        // prepare parameters
        String token = randomString();
        // mock
        OAuth2AccessTokenEntity accessTokenEntity = randomPojo(OAuth2AccessTokenEntity.class, o -> o.setUserId(1L)
                .setUserType(UserTypeEnum.ADMIN.getValue()));
        when(oauth2TokenService.removeAccessToken(eq(token))).thenReturn(accessTokenEntity);

        // invoke
        authService.logout(token, LoginLogTypeEnum.LOGOUT_SELF.getType());
        // verify call parameters
        verify(loginLogService).createLoginLog(
                argThat(o -> o.getLogType().equals(LoginLogTypeEnum.LOGOUT_SELF.getType())
                        && o.getResult().equals(LoginResultEnum.SUCCESS.getResult()))
        );
        // invoke, and verify

    }

    @Test
    public void testLogout_fail() {
        // prepare parameters
        String token = randomString();

        // invoke
        authService.logout(token, LoginLogTypeEnum.LOGOUT_SELF.getType());
        // verify call parameters
        verify(loginLogService, never()).createLoginLog(any());
    }

}
