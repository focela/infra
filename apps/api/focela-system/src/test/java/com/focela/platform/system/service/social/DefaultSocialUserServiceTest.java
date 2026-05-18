package com.focela.platform.system.service.social;

import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.api.social.dto.SocialUserBindRpcRequest;
import com.focela.platform.system.api.social.dto.SocialUserRpcResponse;
import com.focela.platform.system.controller.admin.social.dto.user.SocialUserPageRequest;
import com.focela.platform.system.entity.social.SocialUserBindEntity;
import com.focela.platform.system.entity.social.SocialUserEntity;
import com.focela.platform.system.repository.mapper.social.SocialUserBindMapper;
import com.focela.platform.system.repository.mapper.social.SocialUserMapper;
import com.focela.platform.system.enums.social.SocialTypeEnum;
import jakarta.annotation.Resource;
import me.zhyd.oauth.model.AuthUser;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static cn.hutool.core.util.RandomUtil.randomEle;
import static cn.hutool.core.util.RandomUtil.randomLong;
import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildBetweenTime;
import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildTime;
import static com.focela.platform.common.utils.json.JsonUtils.toJsonString;
import static com.focela.platform.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.test.core.utils.RandomUtils.randomPojo;
import static com.focela.platform.test.core.utils.RandomUtils.randomString;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.SOCIAL_USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

/**
 * {@link DefaultSocialUserService}  unit test class
 */
@Import(DefaultSocialUserService.class)
public class DefaultSocialUserServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultSocialUserService socialUserService;

    @Resource
    private SocialUserMapper socialUserMapper;
    @Resource
    private SocialUserBindMapper socialUserBindMapper;

    @MockitoBean
    private SocialClientService socialClientService;

    @Test
    public void testGetSocialUserList() {
        Long userId = 1L;
        Integer userType = UserTypeEnum.ADMIN.getValue();
        // mock get social user
        SocialUserEntity socialUser = randomPojo(SocialUserEntity.class).setType(SocialTypeEnum.GITEE.getType());
        socialUserMapper.insert(socialUser); // can be queried
        socialUserMapper.insert(randomPojo(SocialUserEntity.class)); // cannot be queried
        // mock get binding
        socialUserBindMapper.insert(randomPojo(SocialUserBindEntity.class) // can be queried
                .setUserId(userId).setUserType(userType).setSocialType(SocialTypeEnum.GITEE.getType())
                .setSocialUserId(socialUser.getId()));
        socialUserBindMapper.insert(randomPojo(SocialUserBindEntity.class) // cannot be queried
                .setUserId(2L).setUserType(userType).setSocialType(SocialTypeEnum.DINGTALK.getType()));

        // invoke
        List<SocialUserEntity> result = socialUserService.getSocialUserList(userId, userType);
        // assert
        assertEquals(1, result.size());
        assertPojoEquals(socialUser, result.get(0));
    }

    @Test
    public void testBindSocialUser() {
        // prepare parameters
        SocialUserBindRpcRequest request = new SocialUserBindRpcRequest()
                .setUserId(1L).setUserType(UserTypeEnum.ADMIN.getValue())
                .setSocialType(SocialTypeEnum.GITEE.getType()).setCode("test_code").setState("test_state");
        // mock data: get social user
        SocialUserEntity socialUser = randomPojo(SocialUserEntity.class).setType(request.getSocialType())
                .setCode(request.getCode()).setState(request.getState());
        socialUserMapper.insert(socialUser);
        // mock data: user may have already bound this social type
        socialUserBindMapper.insert(randomPojo(SocialUserBindEntity.class).setUserId(1L).setUserType(UserTypeEnum.ADMIN.getValue())
                .setSocialType(SocialTypeEnum.GITEE.getType()).setSocialUserId(-1L));
        // mock data: social user may have been bound to another user before
        socialUserBindMapper.insert(randomPojo(SocialUserBindEntity.class).setUserType(UserTypeEnum.ADMIN.getValue())
                .setSocialType(SocialTypeEnum.GITEE.getType()).setSocialUserId(socialUser.getId()));

        // invoke
        String openid = socialUserService.bindSocialUser(request);
        // assert
        List<SocialUserBindEntity> socialUserBinds = socialUserBindMapper.selectList();
        assertEquals(1, socialUserBinds.size());
        assertEquals(socialUser.getOpenid(), openid);
    }

    @Test
    public void testUnbindSocialUser_success() {
        // prepare parameters
        Long userId = 1L;
        Integer userType = UserTypeEnum.ADMIN.getValue();
        Integer type = SocialTypeEnum.GITEE.getType();
        String openid = "test_openid";
        // mock data: social user
        SocialUserEntity socialUser = randomPojo(SocialUserEntity.class).setType(type).setOpenid(openid);
        socialUserMapper.insert(socialUser);
        // mock data: social binding
        SocialUserBindEntity socialUserBind = randomPojo(SocialUserBindEntity.class).setUserType(userType)
                .setUserId(userId).setSocialType(type);
        socialUserBindMapper.insert(socialUserBind);

        // invoke
        socialUserService.unbindSocialUser(userId, userType, type, openid);
        // assert
        assertEquals(0, socialUserBindMapper.selectCount(null).intValue());
    }

    @Test
    public void testUnbindSocialUser_notFound() {
        // invoke, and assert
        assertServiceException(
                () -> socialUserService.unbindSocialUser(randomLong(), UserTypeEnum.ADMIN.getValue(),
                        SocialTypeEnum.GITEE.getType(), "test_openid"),
                SOCIAL_USER_NOT_FOUND);
    }

    @Test
    public void testGetSocialUser() {
        // prepare parameters
        Integer userType = UserTypeEnum.ADMIN.getValue();
        Integer type = SocialTypeEnum.GITEE.getType();
        String code = "tudou";
        String state = "yuanma";
        // mock social user
        SocialUserEntity socialUserEntity = randomPojo(SocialUserEntity.class).setType(type).setCode(code).setState(state);
        socialUserMapper.insert(socialUserEntity);
        // mock social user binding
        Long userId = randomLong();
        SocialUserBindEntity socialUserBind = randomPojo(SocialUserBindEntity.class).setUserType(userType).setUserId(userId)
                .setSocialType(type).setSocialUserId(socialUserEntity.getId());
        socialUserBindMapper.insert(socialUserBind);

        // invoke
        SocialUserRpcResponse socialUser = socialUserService.getSocialUserByCode(userType, type, code, state);
        // assert
        assertEquals(userId, socialUser.getUserId());
        assertEquals(socialUserEntity.getOpenid(), socialUser.getOpenid());
    }

    @Test
    public void testAuthSocialUser_exists() {
        // prepare parameters
        Integer socialType = SocialTypeEnum.GITEE.getType();
        Integer userType = randomEle(SocialTypeEnum.values()).getType();
        String code = "tudou";
        String state = "yuanma";
        // mock the method
        SocialUserEntity socialUser = randomPojo(SocialUserEntity.class).setType(socialType).setCode(code).setState(state);
        socialUserMapper.insert(socialUser);

        // invoke
        SocialUserEntity result = socialUserService.authSocialUser(socialType, userType, code, state);
        // assert
        assertPojoEquals(socialUser, result);
    }

    @Test
    public void testAuthSocialUser_notNull() {
        // mock data
        SocialUserEntity socialUser = randomPojo(SocialUserEntity.class,
                o -> o.setType(SocialTypeEnum.GITEE.getType()).setCode("tudou").setState("yuanma"));
        socialUserMapper.insert(socialUser);
        // prepare parameters
        Integer socialType = SocialTypeEnum.GITEE.getType();
        Integer userType = randomEle(SocialTypeEnum.values()).getType();
        String code = "tudou";
        String state = "yuanma";

        // invoke
        SocialUserEntity result = socialUserService.authSocialUser(socialType, userType, code, state);
        // assert
        assertPojoEquals(socialUser, result);
    }

    @Test
    public void testAuthSocialUser_insert() {
        // prepare parameters
        Integer socialType = SocialTypeEnum.GITEE.getType();
        Integer userType = randomEle(SocialTypeEnum.values()).getType();
        String code = "tudou";
        String state = "yuanma";
        // mock the method
        AuthUser authUser = randomPojo(AuthUser.class);
        when(socialClientService.getAuthUser(eq(socialType), eq(userType), eq(code), eq(state))).thenReturn(authUser);

        // invoke
        SocialUserEntity result = socialUserService.authSocialUser(socialType, userType, code, state);
        // assert
        assertBindSocialUser(socialType, result, authUser);
        assertEquals(code, result.getCode());
        assertEquals(state, result.getState());
    }

    @Test
    public void testAuthSocialUser_update() {
        // prepare parameters
        Integer socialType = SocialTypeEnum.GITEE.getType();
        Integer userType = randomEle(SocialTypeEnum.values()).getType();
        String code = "tudou";
        String state = "yuanma";
        // mock data
        socialUserMapper.insert(randomPojo(SocialUserEntity.class).setType(socialType).setOpenid("test_openid"));
        // mock the method
        AuthUser authUser = randomPojo(AuthUser.class);
        when(socialClientService.getAuthUser(eq(socialType), eq(userType), eq(code), eq(state))).thenReturn(authUser);

        // invoke
        SocialUserEntity result = socialUserService.authSocialUser(socialType, userType, code, state);
        // assert
        assertBindSocialUser(socialType, result, authUser);
        assertEquals(code, result.getCode());
        assertEquals(state, result.getState());
    }

    private void assertBindSocialUser(Integer type, SocialUserEntity socialUser, AuthUser authUser) {
        assertEquals(authUser.getToken().getAccessToken(), socialUser.getToken());
        assertEquals(toJsonString(authUser.getToken()), socialUser.getRawTokenInfo());
        assertEquals(authUser.getNickname(), socialUser.getNickname());
        assertEquals(authUser.getAvatar(), socialUser.getAvatar());
        assertEquals(toJsonString(authUser.getRawUserInfo()), socialUser.getRawUserInfo());
        assertEquals(type, socialUser.getType());
        assertEquals(authUser.getUuid(), socialUser.getOpenid());
    }

    @Test
    public void testGetSocialUser_id() {
        // mock data
        SocialUserEntity socialUserEntity = randomPojo(SocialUserEntity.class);
        socialUserMapper.insert(socialUserEntity);
        // prepare parameters
        Long id = socialUserEntity.getId();

        // invoke
        SocialUserEntity dbSocialUserEntity = socialUserService.getSocialUser(id);
        // assert
        assertPojoEquals(socialUserEntity, dbSocialUserEntity);
    }

    @Test
    public void testGetSocialUserPage() {
        // mock data
        SocialUserEntity dbSocialUser = randomPojo(SocialUserEntity.class, o -> { // will be queried later
            o.setType(SocialTypeEnum.GITEE.getType());
            o.setNickname("Focela");
            o.setOpenid("focelasecret");
            o.setCreateTime(buildTime(2020, 1, 15));
        });
        socialUserMapper.insert(dbSocialUser);
        // test type mismatch
        socialUserMapper.insert(cloneIgnoreId(dbSocialUser, o -> o.setType(SocialTypeEnum.DINGTALK.getType())));
        // test nickname mismatch
        socialUserMapper.insert(cloneIgnoreId(dbSocialUser, o -> o.setNickname(randomString())));
        // test openid mismatch
        socialUserMapper.insert(cloneIgnoreId(dbSocialUser, o -> o.setOpenid("java")));
        // test createTime mismatch
        socialUserMapper.insert(cloneIgnoreId(dbSocialUser, o -> o.setCreateTime(buildTime(2020, 1, 21))));
        // prepare parameters
        SocialUserPageRequest request = new SocialUserPageRequest();
        request.setType(SocialTypeEnum.GITEE.getType());
        request.setNickname("Focela");
        request.setOpenid("focela");
        request.setCreateTime(buildBetweenTime(2020, 1, 10, 2020, 1, 20));

        // invoke
        PageResult<SocialUserEntity> pageResult = socialUserService.getSocialUserPage(request);
        // assert
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbSocialUser, pageResult.getList().get(0));
    }

}
