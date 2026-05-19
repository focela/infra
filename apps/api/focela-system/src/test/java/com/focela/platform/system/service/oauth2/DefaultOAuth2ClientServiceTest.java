package com.focela.platform.system.service.oauth2;

import cn.hutool.extra.spring.SpringUtil;
import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.controller.admin.oauth2.dto.client.OAuth2ClientPageRequest;
import com.focela.platform.system.controller.admin.oauth2.dto.client.OAuth2ClientSaveRequest;
import com.focela.platform.system.domain.entity.oauth2.OAuth2ClientEntity;
import com.focela.platform.system.repository.mapper.oauth2.OAuth2ClientMapper;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.context.annotation.Import;

import jakarta.annotation.Resource;
import java.util.Collections;

import static com.focela.platform.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.test.core.utils.RandomUtils.*;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

/**
 * {@link DefaultOAuth2ClientService}  unit test class
 */
@Import(DefaultOAuth2ClientService.class)
public class DefaultOAuth2ClientServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultOAuth2ClientService oauth2ClientService;

    @Resource
    private OAuth2ClientMapper oauth2ClientMapper;

    @Test
    public void testCreateOAuth2Client_success() {
        // prepare parameters
        OAuth2ClientSaveRequest request = randomPojo(OAuth2ClientSaveRequest.class,
                o -> o.setLogo(randomString()))
                .setId(null); // prevent id from being assigned

        // invoke
        Long oauth2ClientId = oauth2ClientService.createOAuth2Client(request);
        // assert
        assertNotNull(oauth2ClientId);
        // verify record properties are correct
        OAuth2ClientEntity oAuth2Client = oauth2ClientMapper.selectById(oauth2ClientId);
        assertPojoEquals(request, oAuth2Client, "id");
    }

    @Test
    public void testUpdateOAuth2Client_success() {
        // mock data
        OAuth2ClientEntity dbOAuth2Client = randomPojo(OAuth2ClientEntity.class);
        oauth2ClientMapper.insert(dbOAuth2Client);// @Sql: first insert an existing record
        // prepare parameters
        OAuth2ClientSaveRequest request = randomPojo(OAuth2ClientSaveRequest.class, o -> {
            o.setId(dbOAuth2Client.getId()); // set updated ID
            o.setLogo(randomString());
        });

        // invoke
        oauth2ClientService.updateOAuth2Client(request);
        // verify update is correct
        OAuth2ClientEntity oAuth2Client = oauth2ClientMapper.selectById(request.getId()); // get the latest
        assertPojoEquals(request, oAuth2Client);
    }

    @Test
    public void testUpdateOAuth2Client_notExists() {
        // prepare parameters
        OAuth2ClientSaveRequest request = randomPojo(OAuth2ClientSaveRequest.class);

        // invoke and assert exception
        assertServiceException(() -> oauth2ClientService.updateOAuth2Client(request), OAUTH2_CLIENT_NOT_EXISTS);
    }

    @Test
    public void testDeleteOAuth2Client_success() {
        // mock data
        OAuth2ClientEntity dbOAuth2Client = randomPojo(OAuth2ClientEntity.class);
        oauth2ClientMapper.insert(dbOAuth2Client);// @Sql: first insert an existing record
        // prepare parameters
        Long id = dbOAuth2Client.getId();

        // invoke
        oauth2ClientService.deleteOAuth2Client(id);
        // verify data no longer exists
        assertNull(oauth2ClientMapper.selectById(id));
    }

    @Test
    public void testDeleteOAuth2Client_notExists() {
        // prepare parameters
        Long id = randomLongId();

        // invoke and assert exception
        assertServiceException(() -> oauth2ClientService.deleteOAuth2Client(id), OAUTH2_CLIENT_NOT_EXISTS);
    }

    @Test
    public void testValidateClientIdExists_withId() {
        // mock data
        OAuth2ClientEntity client = randomPojo(OAuth2ClientEntity.class).setClientId("tudou");
        oauth2ClientMapper.insert(client);
        // prepare parameters
        Long id = randomLongId();
        String clientId = "tudou";

        // invoke, no error
        assertServiceException(() -> oauth2ClientService.validateClientIdExists(id, clientId), OAUTH2_CLIENT_EXISTS);
    }

    @Test
    public void testValidateClientIdExists_noId() {
        // mock data
        OAuth2ClientEntity client = randomPojo(OAuth2ClientEntity.class).setClientId("tudou");
        oauth2ClientMapper.insert(client);
        // prepare parameters
        String clientId = "tudou";

        // invoke, no error
        assertServiceException(() -> oauth2ClientService.validateClientIdExists(null, clientId), OAUTH2_CLIENT_EXISTS);
    }

    @Test
    public void testGetOAuth2Client() {
        // mock data
        OAuth2ClientEntity clientEntity = randomPojo(OAuth2ClientEntity.class);
        oauth2ClientMapper.insert(clientEntity);
        // prepare parameters
        Long id = clientEntity.getId();

        // invoke, and assert
        OAuth2ClientEntity dbClientEntity = oauth2ClientService.getOAuth2Client(id);
        assertPojoEquals(clientEntity, dbClientEntity);
    }

    @Test
    public void testGetOAuth2ClientFromCache() {
        // mock data
        OAuth2ClientEntity clientEntity = randomPojo(OAuth2ClientEntity.class);
        oauth2ClientMapper.insert(clientEntity);
        // prepare parameters
        String clientId = clientEntity.getClientId();

        // invoke, and assert
        OAuth2ClientEntity dbClientEntity = oauth2ClientService.getOAuth2ClientFromCache(clientId);
        assertPojoEquals(clientEntity, dbClientEntity);
    }

    @Test
    public void testGetOAuth2ClientPage() {
        // mock data
        OAuth2ClientEntity dbOAuth2Client = randomPojo(OAuth2ClientEntity.class, o -> { // will be queried later
            o.setName("Hidden Dragon");
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
        });
        oauth2ClientMapper.insert(dbOAuth2Client);
        // test name mismatch
        oauth2ClientMapper.insert(cloneIgnoreId(dbOAuth2Client, o -> o.setName("Phoenix")));
        // test status mismatch
        oauth2ClientMapper.insert(cloneIgnoreId(dbOAuth2Client, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));
        // prepare parameters
        OAuth2ClientPageRequest request = new OAuth2ClientPageRequest();
        request.setName("Dragon");
        request.setStatus(CommonStatusEnum.ENABLE.getStatus());

        // invoke
        PageResult<OAuth2ClientEntity> pageResult = oauth2ClientService.getOAuth2ClientPage(request);
        // assert
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbOAuth2Client, pageResult.getList().get(0));
    }

    @Test
    public void testValidOAuthClientFromCache() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(DefaultOAuth2ClientService.class)))
                    .thenReturn(oauth2ClientService);

            // mock the method
            OAuth2ClientEntity client = randomPojo(OAuth2ClientEntity.class).setClientId("default")
                    .setStatus(CommonStatusEnum.ENABLE.getStatus());
            oauth2ClientMapper.insert(client);
            OAuth2ClientEntity client02 = randomPojo(OAuth2ClientEntity.class).setClientId("disable")
                    .setStatus(CommonStatusEnum.DISABLE.getStatus());
            oauth2ClientMapper.insert(client02);

            // invoke, and assert
            assertServiceException(() -> oauth2ClientService.validateOAuthClientFromCache(randomString(),
                    null, null, null, null), OAUTH2_CLIENT_NOT_EXISTS);
            assertServiceException(() -> oauth2ClientService.validateOAuthClientFromCache("disable",
                    null, null, null, null), OAUTH2_CLIENT_DISABLE);
            assertServiceException(() -> oauth2ClientService.validateOAuthClientFromCache("default",
                    randomString(), null, null, null), OAUTH2_CLIENT_CLIENT_SECRET_ERROR);
            assertServiceException(() -> oauth2ClientService.validateOAuthClientFromCache("default",
                    null, randomString(), null, null), OAUTH2_CLIENT_AUTHORIZED_GRANT_TYPE_NOT_EXISTS);
            assertServiceException(() -> oauth2ClientService.validateOAuthClientFromCache("default",
                    null, null, Collections.singleton(randomString()), null), OAUTH2_CLIENT_SCOPE_OVER);
            assertServiceException(() -> oauth2ClientService.validateOAuthClientFromCache("default",
                    null, null, null, "test"), OAUTH2_CLIENT_REDIRECT_URI_NOT_MATCH, "test");
            // successful invoke（1：parameters are complete）
            OAuth2ClientEntity result = oauth2ClientService.validateOAuthClientFromCache(client.getClientId(), client.getSecret(),
                    client.getAuthorizedGrantTypes().get(0), client.getScopes(), client.getRedirectUris().get(0));
            assertPojoEquals(client, result);
            // successful invoke（2：only clientId parameter）
            result = oauth2ClientService.validateOAuthClientFromCache(client.getClientId());
            assertPojoEquals(client, result);
        }
    }

}
