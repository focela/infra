package com.focela.platform.module.system.service.oauth2;

import cn.hutool.core.util.RandomUtil;
import com.focela.platform.framework.common.enums.UserTypeEnum;
import com.focela.platform.framework.common.utils.date.DateUtils;
import com.focela.platform.framework.test.core.support.BaseDbUnitTest;
import com.focela.platform.module.system.repository.entity.oauth2.OAuth2CodeEntity;
import com.focela.platform.module.system.repository.mapper.oauth2.OAuth2CodeMapper;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

import static com.focela.platform.framework.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.framework.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.framework.test.core.utils.RandomUtils.*;
import static com.focela.platform.module.system.enums.ErrorCodeConstants.OAUTH2_CODE_EXPIRE;
import static com.focela.platform.module.system.enums.ErrorCodeConstants.OAUTH2_CODE_NOT_EXISTS;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link DefaultOAuth2CodeService} 的单元测试类
 *
 * @author 芋道源码
 */
@Import(DefaultOAuth2CodeService.class)
class DefaultOAuth2CodeServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultOAuth2CodeService oauth2CodeService;

    @Resource
    private OAuth2CodeMapper oauth2CodeMapper;

    @Test
    public void testCreateAuthorizationCode() {
        // 准备参数
        Long userId = randomLongId();
        Integer userType = RandomUtil.randomEle(UserTypeEnum.values()).getValue();
        String clientId = randomString();
        List<String> scopes = Lists.newArrayList("read", "write");
        String redirectUri = randomString();
        String state = randomString();

        // 调用
        OAuth2CodeEntity codeDO = oauth2CodeService.createAuthorizationCode(userId, userType, clientId,
                scopes, redirectUri, state);
        // 断言
        OAuth2CodeEntity dbCodeDO = oauth2CodeMapper.selectByCode(codeDO.getCode());
        // TODO:  expiresTime 被屏蔽，仅 win11 会复现，建议后续修复。
        assertPojoEquals(codeDO, dbCodeDO, "expiresTime", "createTime", "updateTime", "deleted");
        assertEquals(userId, codeDO.getUserId());
        assertEquals(userType, codeDO.getUserType());
        assertEquals(clientId, codeDO.getClientId());
        assertEquals(scopes, codeDO.getScopes());
        assertEquals(redirectUri, codeDO.getRedirectUri());
        assertEquals(state, codeDO.getState());
        assertFalse(DateUtils.isExpired(codeDO.getExpiresTime()));
    }

    @Test
    public void testConsumeAuthorizationCode_null() {
        // 调用，并断言
        assertServiceException(() -> oauth2CodeService.consumeAuthorizationCode(randomString()),
                OAUTH2_CODE_NOT_EXISTS);
    }

    @Test
    public void testConsumeAuthorizationCode_expired() {
        // 准备参数
        String code = "test_code";
        // mock 数据
        OAuth2CodeEntity codeDO = randomPojo(OAuth2CodeEntity.class).setCode(code)
                .setExpiresTime(LocalDateTime.now().minusDays(1));
        oauth2CodeMapper.insert(codeDO);

        // 调用，并断言
        assertServiceException(() -> oauth2CodeService.consumeAuthorizationCode(code),
                OAUTH2_CODE_EXPIRE);
    }

    @Test
    public void testConsumeAuthorizationCode_success() {
        // 准备参数
        String code = "test_code";
        // mock 数据
        OAuth2CodeEntity codeDO = randomPojo(OAuth2CodeEntity.class).setCode(code)
                .setExpiresTime(LocalDateTime.now().plusDays(1));
        oauth2CodeMapper.insert(codeDO);

        // 调用
        OAuth2CodeEntity result = oauth2CodeService.consumeAuthorizationCode(code);
        // TODO:  expiresTime 被屏蔽，仅 win11 会复现，建议后续修复。
        assertPojoEquals(codeDO, result, "expiresTime");
        assertNull(oauth2CodeMapper.selectByCode(code));
    }

}
