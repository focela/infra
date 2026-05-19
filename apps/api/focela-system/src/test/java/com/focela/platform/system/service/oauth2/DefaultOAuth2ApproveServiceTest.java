package com.focela.platform.system.service.oauth2;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.common.utils.date.DateUtils;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.domain.entity.oauth2.OAuth2ApproveEntity;
import com.focela.platform.system.domain.entity.oauth2.OAuth2ClientEntity;
import com.focela.platform.system.repository.mapper.oauth2.OAuth2ApproveMapper;
import jakarta.annotation.Resource;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static cn.hutool.core.util.RandomUtil.*;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.RandomUtils.*;
import static com.focela.platform.test.core.utils.RandomUtils.randomString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * {@link DefaultOAuth2ApproveService}  unit test class
 */
@Import(DefaultOAuth2ApproveService.class)
public class DefaultOAuth2ApproveServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultOAuth2ApproveService oauth2ApproveService;

    @Resource
    private OAuth2ApproveMapper oauth2ApproveMapper;

    @MockitoBean
    private OAuth2ClientService oauth2ClientService;

    @Test
    public void checkForPreApproval_clientAutoApprove() {
        // prepare parameters
        Long userId = randomLongId();
        Integer userType = randomEle(UserTypeEnum.values()).getValue();
        String clientId = randomString();
        List<String> requestedScopes = Lists.newArrayList("read");
        // mock the method
        when(oauth2ClientService.validateOAuthClientFromCache(eq(clientId)))
                .thenReturn(randomPojo(OAuth2ClientEntity.class).setAutoApproveScopes(requestedScopes));

        // invoke
        boolean success = oauth2ApproveService.checkForPreApproval(userId, userType,
                clientId, requestedScopes);
        // assert
        assertTrue(success);
        List<OAuth2ApproveEntity> result = oauth2ApproveMapper.selectList();
        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getUserId());
        assertEquals(userType, result.get(0).getUserType());
        assertEquals(clientId, result.get(0).getClientId());
        assertEquals("read", result.get(0).getScope());
        assertTrue(result.get(0).getApproved());
        assertFalse(DateUtils.isExpired(result.get(0).getExpiresTime()));
    }

    @Test
    public void checkForPreApproval_approve() {
        // prepare parameters
        Long userId = randomLongId();
        Integer userType = randomEle(UserTypeEnum.values()).getValue();
        String clientId = randomString();
        List<String> requestedScopes = Lists.newArrayList("read");
        // mock the method
        when(oauth2ClientService.validateOAuthClientFromCache(eq(clientId)))
                .thenReturn(randomPojo(OAuth2ClientEntity.class).setAutoApproveScopes(null));
        // mock data
        OAuth2ApproveEntity approve = randomPojo(OAuth2ApproveEntity.class).setUserId(userId)
                .setUserType(userType).setClientId(clientId).setScope("read")
                .setExpiresTime(LocalDateTimeUtil.offset(LocalDateTime.now(), 1L, ChronoUnit.DAYS)).setApproved(true); // agree
        oauth2ApproveMapper.insert(approve);

        // invoke
        boolean success = oauth2ApproveService.checkForPreApproval(userId, userType,
                clientId, requestedScopes);
        // assert
        assertTrue(success);
    }

    @Test
    public void checkForPreApproval_reject() {
        // prepare parameters
        Long userId = randomLongId();
        Integer userType = randomEle(UserTypeEnum.values()).getValue();
        String clientId = randomString();
        List<String> requestedScopes = Lists.newArrayList("read");
        // mock the method
        when(oauth2ClientService.validateOAuthClientFromCache(eq(clientId)))
                .thenReturn(randomPojo(OAuth2ClientEntity.class).setAutoApproveScopes(null));
        // mock data
        OAuth2ApproveEntity approve = randomPojo(OAuth2ApproveEntity.class).setUserId(userId)
                .setUserType(userType).setClientId(clientId).setScope("read")
                .setExpiresTime(LocalDateTimeUtil.offset(LocalDateTime.now(), 1L, ChronoUnit.DAYS)).setApproved(false); // reject
        oauth2ApproveMapper.insert(approve);

        // invoke
        boolean success = oauth2ApproveService.checkForPreApproval(userId, userType,
                clientId, requestedScopes);
        // assert
        assertFalse(success);
    }

    @Test
    public void testUpdateAfterApproval_none() {
        // prepare parameters
        Long userId = randomLongId();
        Integer userType = randomEle(UserTypeEnum.values()).getValue();
        String clientId = randomString();

        // invoke
        boolean success = oauth2ApproveService.updateAfterApproval(userId, userType, clientId,
                null);
        // assert
        assertTrue(success);
        List<OAuth2ApproveEntity> result = oauth2ApproveMapper.selectList();
        assertEquals(0, result.size());
    }

    @Test
    public void testUpdateAfterApproval_approved() {
        // prepare parameters
        Long userId = randomLongId();
        Integer userType = randomEle(UserTypeEnum.values()).getValue();
        String clientId = randomString();
        Map<String, Boolean> requestedScopes = new LinkedHashMap<>(); // ordered for easier assertion
        requestedScopes.put("read", true);
        requestedScopes.put("write", false);
        // mock the method

        // invoke
        boolean success = oauth2ApproveService.updateAfterApproval(userId, userType, clientId,
                requestedScopes);
        // assert
        assertTrue(success);
        List<OAuth2ApproveEntity> result = oauth2ApproveMapper.selectList();
        assertEquals(2, result.size());
        // read
        assertEquals(userId, result.get(0).getUserId());
        assertEquals(userType, result.get(0).getUserType());
        assertEquals(clientId, result.get(0).getClientId());
        assertEquals("read", result.get(0).getScope());
        assertTrue(result.get(0).getApproved());
        assertFalse(DateUtils.isExpired(result.get(0).getExpiresTime()));
        // write
        assertEquals(userId, result.get(1).getUserId());
        assertEquals(userType, result.get(1).getUserType());
        assertEquals(clientId, result.get(1).getClientId());
        assertEquals("write", result.get(1).getScope());
        assertFalse(result.get(1).getApproved());
        assertFalse(DateUtils.isExpired(result.get(1).getExpiresTime()));
    }

    @Test
    public void testUpdateAfterApproval_reject() {
        // prepare parameters
        Long userId = randomLongId();
        Integer userType = randomEle(UserTypeEnum.values()).getValue();
        String clientId = randomString();
        Map<String, Boolean> requestedScopes = new LinkedHashMap<>();
        requestedScopes.put("write", false);
        // mock the method

        // invoke
        boolean success = oauth2ApproveService.updateAfterApproval(userId, userType, clientId,
                requestedScopes);
        // assert
        assertFalse(success);
        List<OAuth2ApproveEntity> result = oauth2ApproveMapper.selectList();
        assertEquals(1, result.size());
        // write
        assertEquals(userId, result.get(0).getUserId());
        assertEquals(userType, result.get(0).getUserType());
        assertEquals(clientId, result.get(0).getClientId());
        assertEquals("write", result.get(0).getScope());
        assertFalse(result.get(0).getApproved());
        assertFalse(DateUtils.isExpired(result.get(0).getExpiresTime()));
    }

    @Test
    public void testGetApproveList() {
        // prepare parameters
        Long userId = 10L;
        Integer userType = UserTypeEnum.ADMIN.getValue();
        String clientId = randomString();
        // mock data
        OAuth2ApproveEntity approve = randomPojo(OAuth2ApproveEntity.class).setUserId(userId)
                .setUserType(userType).setClientId(clientId).setExpiresTime(LocalDateTimeUtil.offset(LocalDateTime.now(), 1L, ChronoUnit.DAYS));
        oauth2ApproveMapper.insert(approve); // not expired
        oauth2ApproveMapper.insert(ObjectUtil.clone(approve).setId(null)
                .setExpiresTime(LocalDateTimeUtil.offset(LocalDateTime.now(), -1L, ChronoUnit.DAYS))); // expired

        // invoke
        List<OAuth2ApproveEntity> result = oauth2ApproveService.getApproveList(userId, userType, clientId);
        // assert
        assertEquals(1, result.size());
        // TODO:  expiresTime blocked, only reproducible on win11, follow-up fix recommended.
        assertPojoEquals(approve, result.get(0), "expiresTime");
    }

    @Test
    public void testSaveApprove_insert() {
        // prepare parameters
        Long userId = randomLongId();
        Integer userType = randomEle(UserTypeEnum.values()).getValue();
        String clientId = randomString();
        String scope = randomString();
        Boolean approved = randomBoolean();
        LocalDateTime expireTime = LocalDateTime.ofInstant(randomDay(1, 30).toInstant(), ZoneId.systemDefault());
        // mock the method

        // invoke
        oauth2ApproveService.saveApprove(userId, userType, clientId,
                scope, approved, expireTime);
        // assert
        List<OAuth2ApproveEntity> result = oauth2ApproveMapper.selectList();
        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getUserId());
        assertEquals(userType, result.get(0).getUserType());
        assertEquals(clientId, result.get(0).getClientId());
        assertEquals(scope, result.get(0).getScope());
        assertEquals(approved, result.get(0).getApproved());
        assertEquals(expireTime, result.get(0).getExpiresTime());
    }

    @Test
    public void testSaveApprove_update() {
        // mock data
        OAuth2ApproveEntity approve = randomPojo(OAuth2ApproveEntity.class);
        oauth2ApproveMapper.insert(approve);
        // prepare parameters
        Long userId = approve.getUserId();
        Integer userType = approve.getUserType();
        String clientId = approve.getClientId();
        String scope = approve.getScope();
        Boolean approved = randomBoolean();
        LocalDateTime expireTime = LocalDateTime.ofInstant(randomDay(1, 30).toInstant(), ZoneId.systemDefault());
        // mock the method

        // invoke
        oauth2ApproveService.saveApprove(userId, userType, clientId,
                scope, approved, expireTime);
        // assert
        List<OAuth2ApproveEntity> result = oauth2ApproveMapper.selectList();
        assertEquals(1, result.size());
        assertEquals(approve.getId(), result.get(0).getId());
        assertEquals(userId, result.get(0).getUserId());
        assertEquals(userType, result.get(0).getUserType());
        assertEquals(clientId, result.get(0).getClientId());
        assertEquals(scope, result.get(0).getScope());
        assertEquals(approved, result.get(0).getApproved());
        assertEquals(expireTime, result.get(0).getExpiresTime());
    }

}
