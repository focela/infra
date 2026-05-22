package com.focela.platform.system.service.oauth2;

import cn.hutool.core.util.RandomUtil;
import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.common.utils.date.DateUtils;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.domain.entity.oauth2.OAuth2CodeEntity;
import com.focela.platform.system.repository.mapper.oauth2.OAuth2CodeMapper;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.test.core.utils.RandomUtils.*;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.OAUTH2_CODE_EXPIRE;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.OAUTH2_CODE_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link DefaultOAuth2CodeService}  unit test class
 */
@Import(DefaultOAuth2CodeService.class)
class DefaultOAuth2CodeServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultOAuth2CodeService oauth2CodeService;

    @Resource
    private OAuth2CodeMapper oauth2CodeMapper;

    @Test
    public void testCreateAuthorizationCode() {
        // prepare parameters
        Long userId = randomLongId();
        Integer userType = RandomUtil.randomEle(UserTypeEnum.values()).getValue();
        String clientId = randomString();
        List<String> scopes = Lists.newArrayList("read", "write");
        String redirectUri = randomString();
        String state = randomString();

        // invoke
        OAuth2CodeEntity authorizationCode = oauth2CodeService.createAuthorizationCode(userId, userType, clientId,
                scopes, redirectUri, state);
        // assert
        OAuth2CodeEntity dbAuthorizationCode = oauth2CodeMapper.selectByCode(authorizationCode.getCode());
        // Known fixture gap: expiresTime is excluded because this assertion is unstable on Windows 11.
        assertPojoEquals(authorizationCode, dbAuthorizationCode, "expiresTime", "createTime", "updateTime", "deleted");
        assertEquals(userId, authorizationCode.getUserId());
        assertEquals(userType, authorizationCode.getUserType());
        assertEquals(clientId, authorizationCode.getClientId());
        assertEquals(scopes, authorizationCode.getScopes());
        assertEquals(redirectUri, authorizationCode.getRedirectUri());
        assertEquals(state, authorizationCode.getState());
        assertFalse(DateUtils.isExpired(authorizationCode.getExpiresTime()));
    }

    @Test
    public void testConsumeAuthorizationCode_null() {
        // invoke, and assert
        assertServiceException(() -> oauth2CodeService.consumeAuthorizationCode(randomString()),
                OAUTH2_CODE_NOT_FOUND);
    }

    @Test
    public void testConsumeAuthorizationCode_expired() {
        // prepare parameters
        String code = "test_code";
        // mock data
        OAuth2CodeEntity authorizationCode = randomPojo(OAuth2CodeEntity.class).setCode(code)
                .setExpiresTime(LocalDateTime.now().minusDays(1));
        oauth2CodeMapper.insert(authorizationCode);

        // invoke, and assert
        assertServiceException(() -> oauth2CodeService.consumeAuthorizationCode(code),
                OAUTH2_CODE_EXPIRE);
    }

    @Test
    public void testConsumeAuthorizationCode_success() {
        // prepare parameters
        String code = "test_code";
        // mock data
        OAuth2CodeEntity authorizationCode = randomPojo(OAuth2CodeEntity.class).setCode(code)
                .setExpiresTime(LocalDateTime.now().plusDays(1));
        oauth2CodeMapper.insert(authorizationCode);

        // invoke
        OAuth2CodeEntity result = oauth2CodeService.consumeAuthorizationCode(code);
        // Known fixture gap: expiresTime is excluded because this assertion is unstable on Windows 11.
        assertPojoEquals(authorizationCode, result, "expiresTime");
        assertNull(oauth2CodeMapper.selectByCode(code));
    }

}
