package com.focela.platform.module.system.service.auth;

import cn.hutool.core.util.ReflectUtil;
import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.common.enums.UserTypeEnum;
import com.focela.platform.framework.test.core.ut.BaseDbUnitTest;
import com.focela.platform.module.system.api.sms.SmsCodeApi;
import com.focela.platform.module.system.api.social.dto.SocialUserBindReqDTO;
import com.focela.platform.module.system.api.social.dto.SocialUserRespDTO;
import com.focela.platform.module.system.controller.admin.auth.dto.*;
import com.focela.platform.module.system.repository.entity.oauth2.OAuth2AccessTokenEntity;
import com.focela.platform.module.system.repository.entity.user.AdminUserEntity;
import com.focela.platform.module.system.enums.logger.LoginLogTypeEnum;
import com.focela.platform.module.system.enums.logger.LoginResultEnum;
import com.focela.platform.module.system.enums.sms.SmsSceneEnum;
import com.focela.platform.module.system.enums.social.SocialTypeEnum;
import com.focela.platform.module.system.service.logger.LoginLogService;
import com.focela.platform.module.system.service.member.MemberService;
import com.focela.platform.module.system.service.oauth2.OAuth2TokenService;
import com.focela.platform.module.system.service.social.SocialUserService;
import com.focela.platform.module.system.service.user.AdminUserService;
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
import static com.focela.platform.framework.test.core.util.AssertUtils.assertPojoEquals;
import static com.focela.platform.framework.test.core.util.AssertUtils.assertServiceException;
import static com.focela.platform.framework.test.core.util.RandomUtils.randomPojo;
import static com.focela.platform.framework.test.core.util.RandomUtils.randomString;
import static com.focela.platform.module.system.enums.ErrorCodeConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@Import(AdminAuthServiceImpl.class)
public class AdminAuthServiceImplTest extends BaseDbUnitTest {

    @Resource
    private AdminAuthServiceImpl authService;

    @MockitoBean
    private AdminUserService userService;
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
        // 注入一个 Validator 对象
        ReflectUtil.setFieldValue(authService, "validator",
                Validation.buildDefaultValidatorFactory().getValidator());
    }

    @Test
    public void testAuthenticate_success() {
        // 准备参数
        String username = randomString();
        String password = randomString();
        // mock user 数据
        AdminUserEntity user = randomPojo(AdminUserEntity.class, o -> o.setUsername(username)
                .setPassword(password).setStatus(CommonStatusEnum.ENABLE.getStatus()));
        when(userService.getUserByUsername(eq(username))).thenReturn(user);
        // mock password 匹配
        when(userService.isPasswordMatch(eq(password), eq(user.getPassword()))).thenReturn(true);

        // 调用
        AdminUserEntity loginUser = authService.authenticate(username, password);
        // 校验
        assertPojoEquals(user, loginUser);
    }

    @Test
    public void testAuthenticate_userNotFound() {
        // 准备参数
        String username = randomString();
        String password = randomString();

        // 调用, 并断言异常
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
        // 准备参数
        String username = randomString();
        String password = randomString();
        // mock user 数据
        AdminUserEntity user = randomPojo(AdminUserEntity.class, o -> o.setUsername(username)
                .setPassword(password).setStatus(CommonStatusEnum.ENABLE.getStatus()));
        when(userService.getUserByUsername(eq(username))).thenReturn(user);

        // 调用, 并断言异常
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
        // 准备参数
        String username = randomString();
        String password = randomString();
        // mock user 数据
        AdminUserEntity user = randomPojo(AdminUserEntity.class, o -> o.setUsername(username)
                .setPassword(password).setStatus(CommonStatusEnum.DISABLE.getStatus()));
        when(userService.getUserByUsername(eq(username))).thenReturn(user);
        // mock password 匹配
        when(userService.isPasswordMatch(eq(password), eq(user.getPassword()))).thenReturn(true);

        // 调用, 并断言异常
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
        // 准备参数
        AuthLoginRequest request = randomPojo(AuthLoginRequest.class, o ->
                o.setUsername("test_username").setPassword("test_password")
                        .setSocialType(randomEle(SocialTypeEnum.values()).getType()));

        // mock 验证码正确
        authService.setCaptchaEnable(false);
        // mock user 数据
        AdminUserEntity user = randomPojo(AdminUserEntity.class, o -> o.setId(1L).setUsername("test_username")
                .setPassword("test_password").setStatus(CommonStatusEnum.ENABLE.getStatus()));
        when(userService.getUserByUsername(eq("test_username"))).thenReturn(user);
        // mock password 匹配
        when(userService.isPasswordMatch(eq("test_password"), eq(user.getPassword()))).thenReturn(true);
        // mock 缓存登录用户到 Redis
        OAuth2AccessTokenEntity accessTokenDO = randomPojo(OAuth2AccessTokenEntity.class, o -> o.setUserId(1L)
                .setUserType(UserTypeEnum.ADMIN.getValue()));
        when(oauth2TokenService.createAccessToken(eq(1L), eq(UserTypeEnum.ADMIN.getValue()), eq("default"), isNull()))
                .thenReturn(accessTokenDO);

        // 调用，并校验
        AuthLoginResponse loginResponse = authService.login(request);
        assertPojoEquals(accessTokenDO, loginResponse);
        // 校验调用参数
        verify(loginLogService).createLoginLog(
                argThat(o -> o.getLogType().equals(LoginLogTypeEnum.LOGIN_USERNAME.getType())
                        && o.getResult().equals(LoginResultEnum.SUCCESS.getResult())
                        && o.getUserId().equals(user.getId()))
        );
        verify(socialUserService).bindSocialUser(eq(new SocialUserBindReqDTO(
                user.getId(), UserTypeEnum.ADMIN.getValue(),
                request.getSocialType(), request.getSocialCode(), request.getSocialState())));
    }

    @Test
    public void testSendSmsCode() {
        // 准备参数
        String mobile = randomString();
        Integer scene = SmsSceneEnum.ADMIN_MEMBER_LOGIN.getScene();
        AuthSmsSendRequest request = new AuthSmsSendRequest(mobile, scene);
        // mock 方法（用户信息）
        AdminUserEntity user = randomPojo(AdminUserEntity.class);
        when(userService.getUserByMobile(eq(mobile))).thenReturn(user);

        // 调用
        authService.sendSmsCode(request);
        // 断言
        verify(smsCodeApi).sendSmsCode(argThat(sendReqDTO -> {
            assertEquals(mobile, sendReqDTO.getMobile());
            assertEquals(scene, sendReqDTO.getScene());
            return true;
        }));
    }

    @Test
    public void testSmsLogin_success() {
        // 准备参数
        String mobile = randomString();
        String code = randomString();
        AuthSmsLoginRequest request = new AuthSmsLoginRequest(mobile, code);
        // mock 方法（验证码）
        doNothing().when(smsCodeApi).useSmsCode((argThat(smsCodeUseReqDTO -> {
            assertEquals(mobile, smsCodeUseReqDTO.getMobile());
            assertEquals(code, smsCodeUseReqDTO.getCode());
            assertEquals(SmsSceneEnum.ADMIN_MEMBER_LOGIN.getScene(), smsCodeUseReqDTO.getScene());
            return true;
        })));
        // mock 方法（用户信息）
        AdminUserEntity user = randomPojo(AdminUserEntity.class, o -> o.setId(1L));
        when(userService.getUserByMobile(eq(mobile))).thenReturn(user);
        // mock 缓存登录用户到 Redis
        OAuth2AccessTokenEntity accessTokenDO = randomPojo(OAuth2AccessTokenEntity.class, o -> o.setUserId(1L)
                .setUserType(UserTypeEnum.ADMIN.getValue()));
        when(oauth2TokenService.createAccessToken(eq(1L), eq(UserTypeEnum.ADMIN.getValue()), eq("default"), isNull()))
                .thenReturn(accessTokenDO);

        // 调用，并断言
        AuthLoginResponse loginResponse = authService.smsLogin(request);
        assertPojoEquals(accessTokenDO, loginResponse);
        // 断言调用
        verify(loginLogService).createLoginLog(
                argThat(o -> o.getLogType().equals(LoginLogTypeEnum.LOGIN_MOBILE.getType())
                        && o.getResult().equals(LoginResultEnum.SUCCESS.getResult())
                        && o.getUserId().equals(user.getId()))
        );
    }

    @Test
    public void testSocialLogin_success() {
        // 准备参数
        AuthSocialLoginRequest request = randomPojo(AuthSocialLoginRequest.class);
        // mock 方法（绑定的用户编号）
        Long userId = 1L;
        when(socialUserService.getSocialUserByCode(eq(UserTypeEnum.ADMIN.getValue()), eq(request.getType()),
                eq(request.getCode()), eq(request.getState()))).thenReturn(new SocialUserRespDTO(randomString(), randomString(), randomString(), userId));
        // mock（用户）
        AdminUserEntity user = randomPojo(AdminUserEntity.class, o -> o.setId(userId));
        when(userService.getUser(eq(userId))).thenReturn(user);
        // mock 缓存登录用户到 Redis
        OAuth2AccessTokenEntity accessTokenDO = randomPojo(OAuth2AccessTokenEntity.class, o -> o.setUserId(1L)
                .setUserType(UserTypeEnum.ADMIN.getValue()));
        when(oauth2TokenService.createAccessToken(eq(1L), eq(UserTypeEnum.ADMIN.getValue()), eq("default"), isNull()))
                .thenReturn(accessTokenDO);

        // 调用，并断言
        AuthLoginResponse loginResponse = authService.socialLogin(request);
        assertPojoEquals(accessTokenDO, loginResponse);
        // 断言调用
        verify(loginLogService).createLoginLog(
                argThat(o -> o.getLogType().equals(LoginLogTypeEnum.LOGIN_SOCIAL.getType())
                        && o.getResult().equals(LoginResultEnum.SUCCESS.getResult())
                        && o.getUserId().equals(user.getId()))
        );
    }

    @Test
    public void testValidateCaptcha_successWithEnable() {
        // 准备参数
        AuthLoginRequest request = randomPojo(AuthLoginRequest.class);

        // mock 验证通过
        when(captchaService.verification(argThat(captchaVO -> {
            assertEquals(request.getCaptchaVerification(), captchaVO.getCaptchaVerification());
            return true;
        }))).thenReturn(ResponseModel.success());

        // 调用，无需断言
        authService.validateCaptcha(request);
    }

    @Test
    public void testValidateCaptcha_successWithDisable() {
        // 准备参数
        AuthLoginRequest request = randomPojo(AuthLoginRequest.class);

        // mock 验证码关闭
        authService.setCaptchaEnable(false);

        // 调用，无需断言
        authService.validateCaptcha(request);
    }

    @Test
    public void testCaptcha_fail() {
        // 准备参数
        AuthLoginRequest request = randomPojo(AuthLoginRequest.class);

        // mock 验证通过
        when(captchaService.verification(argThat(captchaVO -> {
            assertEquals(request.getCaptchaVerification(), captchaVO.getCaptchaVerification());
            return true;
        }))).thenReturn(ResponseModel.errorMsg("就是不对"));

        // 调用, 并断言异常
        assertServiceException(() -> authService.validateCaptcha(request), AUTH_LOGIN_CAPTCHA_CODE_ERROR, "就是不对");
        // 校验调用参数
        verify(loginLogService).createLoginLog(
                argThat(o -> o.getLogType().equals(LoginLogTypeEnum.LOGIN_USERNAME.getType())
                        && o.getResult().equals(LoginResultEnum.CAPTCHA_CODE_ERROR.getResult()))
        );
    }

    @Test
    public void testRefreshToken() {
        // 准备参数
        String refreshToken = randomString();
        // mock 方法
        OAuth2AccessTokenEntity accessTokenDO = randomPojo(OAuth2AccessTokenEntity.class);
        when(oauth2TokenService.refreshAccessToken(eq(refreshToken), eq("default")))
                .thenReturn(accessTokenDO);

        // 调用
        AuthLoginResponse loginResponse = authService.refreshToken(refreshToken);
        // 断言
        assertPojoEquals(accessTokenDO, loginResponse);
    }

    @Test
    public void testLogout_success() {
        // 准备参数
        String token = randomString();
        // mock
        OAuth2AccessTokenEntity accessTokenDO = randomPojo(OAuth2AccessTokenEntity.class, o -> o.setUserId(1L)
                .setUserType(UserTypeEnum.ADMIN.getValue()));
        when(oauth2TokenService.removeAccessToken(eq(token))).thenReturn(accessTokenDO);

        // 调用
        authService.logout(token, LoginLogTypeEnum.LOGOUT_SELF.getType());
        // 校验调用参数
        verify(loginLogService).createLoginLog(
                argThat(o -> o.getLogType().equals(LoginLogTypeEnum.LOGOUT_SELF.getType())
                        && o.getResult().equals(LoginResultEnum.SUCCESS.getResult()))
        );
        // 调用，并校验

    }

    @Test
    public void testLogout_fail() {
        // 准备参数
        String token = randomString();

        // 调用
        authService.logout(token, LoginLogTypeEnum.LOGOUT_SELF.getType());
        // 校验调用参数
        verify(loginLogService, never()).createLoginLog(any());
    }

}
