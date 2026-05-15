package com.focela.platform.module.system.service.oauth2;

import cn.hutool.extra.spring.SpringUtil;
import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.test.core.support.BaseDbUnitTest;
import com.focela.platform.module.system.controller.admin.oauth2.dto.client.OAuth2ClientPageRequest;
import com.focela.platform.module.system.controller.admin.oauth2.dto.client.OAuth2ClientSaveRequest;
import com.focela.platform.module.system.entity.oauth2.OAuth2ClientEntity;
import com.focela.platform.module.system.repository.mapper.oauth2.OAuth2ClientMapper;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.context.annotation.Import;

import jakarta.annotation.Resource;
import java.util.Collections;

import static com.focela.platform.framework.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.framework.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.framework.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.framework.test.core.utils.RandomUtils.*;
import static com.focela.platform.module.system.constants.ErrorCodeConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

/**
 * {@link DefaultOAuth2ClientService} 的单元测试类
 */
@Import(DefaultOAuth2ClientService.class)
public class DefaultOAuth2ClientServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultOAuth2ClientService oauth2ClientService;

    @Resource
    private OAuth2ClientMapper oauth2ClientMapper;

    @Test
    public void testCreateOAuth2Client_success() {
        // 准备参数
        OAuth2ClientSaveRequest request = randomPojo(OAuth2ClientSaveRequest.class,
                o -> o.setLogo(randomString()))
                .setId(null); // 防止 id 被赋值

        // 调用
        Long oauth2ClientId = oauth2ClientService.createOAuth2Client(request);
        // 断言
        assertNotNull(oauth2ClientId);
        // 校验记录的属性是否正确
        OAuth2ClientEntity oAuth2Client = oauth2ClientMapper.selectById(oauth2ClientId);
        assertPojoEquals(request, oAuth2Client, "id");
    }

    @Test
    public void testUpdateOAuth2Client_success() {
        // mock 数据
        OAuth2ClientEntity dbOAuth2Client = randomPojo(OAuth2ClientEntity.class);
        oauth2ClientMapper.insert(dbOAuth2Client);// @Sql: 先插入出一条存在的数据
        // 准备参数
        OAuth2ClientSaveRequest request = randomPojo(OAuth2ClientSaveRequest.class, o -> {
            o.setId(dbOAuth2Client.getId()); // 设置更新的 ID
            o.setLogo(randomString());
        });

        // 调用
        oauth2ClientService.updateOAuth2Client(request);
        // 校验是否更新正确
        OAuth2ClientEntity oAuth2Client = oauth2ClientMapper.selectById(request.getId()); // 获取最新的
        assertPojoEquals(request, oAuth2Client);
    }

    @Test
    public void testUpdateOAuth2Client_notExists() {
        // 准备参数
        OAuth2ClientSaveRequest request = randomPojo(OAuth2ClientSaveRequest.class);

        // 调用, 并断言异常
        assertServiceException(() -> oauth2ClientService.updateOAuth2Client(request), OAUTH2_CLIENT_NOT_EXISTS);
    }

    @Test
    public void testDeleteOAuth2Client_success() {
        // mock 数据
        OAuth2ClientEntity dbOAuth2Client = randomPojo(OAuth2ClientEntity.class);
        oauth2ClientMapper.insert(dbOAuth2Client);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbOAuth2Client.getId();

        // 调用
        oauth2ClientService.deleteOAuth2Client(id);
        // 校验数据不存在了
        assertNull(oauth2ClientMapper.selectById(id));
    }

    @Test
    public void testDeleteOAuth2Client_notExists() {
        // 准备参数
        Long id = randomLongId();

        // 调用, 并断言异常
        assertServiceException(() -> oauth2ClientService.deleteOAuth2Client(id), OAUTH2_CLIENT_NOT_EXISTS);
    }

    @Test
    public void testValidateClientIdExists_withId() {
        // mock 数据
        OAuth2ClientEntity client = randomPojo(OAuth2ClientEntity.class).setClientId("tudou");
        oauth2ClientMapper.insert(client);
        // 准备参数
        Long id = randomLongId();
        String clientId = "tudou";

        // 调用，不会报错
        assertServiceException(() -> oauth2ClientService.validateClientIdExists(id, clientId), OAUTH2_CLIENT_EXISTS);
    }

    @Test
    public void testValidateClientIdExists_noId() {
        // mock 数据
        OAuth2ClientEntity client = randomPojo(OAuth2ClientEntity.class).setClientId("tudou");
        oauth2ClientMapper.insert(client);
        // 准备参数
        String clientId = "tudou";

        // 调用，不会报错
        assertServiceException(() -> oauth2ClientService.validateClientIdExists(null, clientId), OAUTH2_CLIENT_EXISTS);
    }

    @Test
    public void testGetOAuth2Client() {
        // mock 数据
        OAuth2ClientEntity clientDO = randomPojo(OAuth2ClientEntity.class);
        oauth2ClientMapper.insert(clientDO);
        // 准备参数
        Long id = clientDO.getId();

        // 调用，并断言
        OAuth2ClientEntity dbClientDO = oauth2ClientService.getOAuth2Client(id);
        assertPojoEquals(clientDO, dbClientDO);
    }

    @Test
    public void testGetOAuth2ClientFromCache() {
        // mock 数据
        OAuth2ClientEntity clientDO = randomPojo(OAuth2ClientEntity.class);
        oauth2ClientMapper.insert(clientDO);
        // 准备参数
        String clientId = clientDO.getClientId();

        // 调用，并断言
        OAuth2ClientEntity dbClientDO = oauth2ClientService.getOAuth2ClientFromCache(clientId);
        assertPojoEquals(clientDO, dbClientDO);
    }

    @Test
    public void testGetOAuth2ClientPage() {
        // mock 数据
        OAuth2ClientEntity dbOAuth2Client = randomPojo(OAuth2ClientEntity.class, o -> { // 等会查询到
            o.setName("潜龙");
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
        });
        oauth2ClientMapper.insert(dbOAuth2Client);
        // 测试 name 不匹配
        oauth2ClientMapper.insert(cloneIgnoreId(dbOAuth2Client, o -> o.setName("凤凰")));
        // 测试 status 不匹配
        oauth2ClientMapper.insert(cloneIgnoreId(dbOAuth2Client, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));
        // 准备参数
        OAuth2ClientPageRequest request = new OAuth2ClientPageRequest();
        request.setName("龙");
        request.setStatus(CommonStatusEnum.ENABLE.getStatus());

        // 调用
        PageResult<OAuth2ClientEntity> pageResult = oauth2ClientService.getOAuth2ClientPage(request);
        // 断言
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbOAuth2Client, pageResult.getList().get(0));
    }

    @Test
    public void testValidOAuthClientFromCache() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(DefaultOAuth2ClientService.class)))
                    .thenReturn(oauth2ClientService);

            // mock 方法
            OAuth2ClientEntity client = randomPojo(OAuth2ClientEntity.class).setClientId("default")
                    .setStatus(CommonStatusEnum.ENABLE.getStatus());
            oauth2ClientMapper.insert(client);
            OAuth2ClientEntity client02 = randomPojo(OAuth2ClientEntity.class).setClientId("disable")
                    .setStatus(CommonStatusEnum.DISABLE.getStatus());
            oauth2ClientMapper.insert(client02);

            // 调用，并断言
            assertServiceException(() -> oauth2ClientService.validOAuthClientFromCache(randomString(),
                    null, null, null, null), OAUTH2_CLIENT_NOT_EXISTS);
            assertServiceException(() -> oauth2ClientService.validOAuthClientFromCache("disable",
                    null, null, null, null), OAUTH2_CLIENT_DISABLE);
            assertServiceException(() -> oauth2ClientService.validOAuthClientFromCache("default",
                    randomString(), null, null, null), OAUTH2_CLIENT_CLIENT_SECRET_ERROR);
            assertServiceException(() -> oauth2ClientService.validOAuthClientFromCache("default",
                    null, randomString(), null, null), OAUTH2_CLIENT_AUTHORIZED_GRANT_TYPE_NOT_EXISTS);
            assertServiceException(() -> oauth2ClientService.validOAuthClientFromCache("default",
                    null, null, Collections.singleton(randomString()), null), OAUTH2_CLIENT_SCOPE_OVER);
            assertServiceException(() -> oauth2ClientService.validOAuthClientFromCache("default",
                    null, null, null, "test"), OAUTH2_CLIENT_REDIRECT_URI_NOT_MATCH, "test");
            // 成功调用（1：参数完整）
            OAuth2ClientEntity result = oauth2ClientService.validOAuthClientFromCache(client.getClientId(), client.getSecret(),
                    client.getAuthorizedGrantTypes().get(0), client.getScopes(), client.getRedirectUris().get(0));
            assertPojoEquals(client, result);
            // 成功调用（2：只有 clientId 参数）
            result = oauth2ClientService.validOAuthClientFromCache(client.getClientId());
            assertPojoEquals(client, result);
        }
    }

}
