package com.focela.platform.system.service.notify;

import cn.hutool.core.map.MapUtil;
import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.test.core.support.BaseMockitoUnitTest;
import com.focela.platform.system.entity.notify.NotifyTemplateEntity;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import static cn.hutool.core.util.RandomUtil.randomEle;
import static com.focela.platform.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.test.core.utils.RandomUtils.*;
import static com.focela.platform.system.constants.ErrorCodeConstants.NOTICE_NOT_FOUND;
import static com.focela.platform.system.constants.ErrorCodeConstants.NOTIFY_SEND_TEMPLATE_PARAM_MISS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DefaultNotifySendServiceTest extends BaseMockitoUnitTest {

    @InjectMocks
    private DefaultNotifySendService notifySendService;

    @Mock
    private NotifyTemplateService notifyTemplateService;
    @Mock
    private NotifyMessageService notifyMessageService;

    @Test
    public void testSendSingleNotifyToAdmin() {
        // prepare parameters
        Long userId = randomLongId();
        String templateCode = randomString();
        Map<String, Object> templateParams = MapUtil.<String, Object>builder().put("code", "1234")
                .put("op", "login").build();
        // mock NotifyTemplateService  method
        NotifyTemplateEntity template = randomPojo(NotifyTemplateEntity.class, o -> {
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
            o.setContent("verification code is {code}, operation is{op}");
            o.setParams(Lists.newArrayList("code", "op"));
        });
        when(notifyTemplateService.getNotifyTemplateByCodeFromCache(eq(templateCode))).thenReturn(template);
        String content = randomString();
        when(notifyTemplateService.formatNotifyTemplateContent(eq(template.getContent()), eq(templateParams)))
                .thenReturn(content);
        // mock NotifyMessageService  method
        Long messageId = randomLongId();
        when(notifyMessageService.createNotifyMessage(eq(userId), eq(UserTypeEnum.ADMIN.getValue()),
                eq(template), eq(content), eq(templateParams))).thenReturn(messageId);

        // invoke
        Long resultMessageId = notifySendService.sendSingleNotifyToAdmin(userId, templateCode, templateParams);
        // assert
        assertEquals(messageId, resultMessageId);
    }

    @Test
    public void testSendSingleNotifyToMember() {
        // prepare parameters
        Long userId = randomLongId();
        String templateCode = randomString();
        Map<String, Object> templateParams = MapUtil.<String, Object>builder().put("code", "1234")
                .put("op", "login").build();
        // mock NotifyTemplateService  method
        NotifyTemplateEntity template = randomPojo(NotifyTemplateEntity.class, o -> {
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
            o.setContent("verification code is {code}, operation is{op}");
            o.setParams(Lists.newArrayList("code", "op"));
        });
        when(notifyTemplateService.getNotifyTemplateByCodeFromCache(eq(templateCode))).thenReturn(template);
        String content = randomString();
        when(notifyTemplateService.formatNotifyTemplateContent(eq(template.getContent()), eq(templateParams)))
                .thenReturn(content);
        // mock NotifyMessageService  method
        Long messageId = randomLongId();
        when(notifyMessageService.createNotifyMessage(eq(userId), eq(UserTypeEnum.MEMBER.getValue()),
                eq(template), eq(content), eq(templateParams))).thenReturn(messageId);

        // invoke
        Long resultMessageId = notifySendService.sendSingleNotifyToMember(userId, templateCode, templateParams);
        // assert
        assertEquals(messageId, resultMessageId);
    }

    /**
     * send succeeds when SMS template is enabled
     */
    @Test
    public void testSendSingleNotify_successWhenMailTemplateEnable() {
        // prepare parameters
        Long userId = randomLongId();
        Integer userType = randomEle(UserTypeEnum.values()).getValue();
        String templateCode = randomString();
        Map<String, Object> templateParams = MapUtil.<String, Object>builder().put("code", "1234")
                .put("op", "login").build();
        // mock NotifyTemplateService  method
        NotifyTemplateEntity template = randomPojo(NotifyTemplateEntity.class, o -> {
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
            o.setContent("verification code is {code}, operation is{op}");
            o.setParams(Lists.newArrayList("code", "op"));
        });
        when(notifyTemplateService.getNotifyTemplateByCodeFromCache(eq(templateCode))).thenReturn(template);
        String content = randomString();
        when(notifyTemplateService.formatNotifyTemplateContent(eq(template.getContent()), eq(templateParams)))
                .thenReturn(content);
        // mock NotifyMessageService  method
        Long messageId = randomLongId();
        when(notifyMessageService.createNotifyMessage(eq(userId), eq(userType),
                eq(template), eq(content), eq(templateParams))).thenReturn(messageId);

        // invoke
        Long resultMessageId = notifySendService.sendSingleNotify(userId, userType, templateCode, templateParams);
        // assert
        assertEquals(messageId, resultMessageId);
    }

    /**
     * send succeeds when SMS template is disabled
     */
    @Test
    public void testSendSingleMail_successWhenSmsTemplateDisable() {
        // prepare parameters
        Long userId = randomLongId();
        Integer userType = randomEle(UserTypeEnum.values()).getValue();
        String templateCode = randomString();
        Map<String, Object> templateParams = MapUtil.<String, Object>builder().put("code", "1234")
                .put("op", "login").build();
        // mock NotifyTemplateService  method
        NotifyTemplateEntity template = randomPojo(NotifyTemplateEntity.class, o -> {
            o.setStatus(CommonStatusEnum.DISABLE.getStatus());
            o.setContent("verification code is {code}, operation is{op}");
            o.setParams(Lists.newArrayList("code", "op"));
        });
        when(notifyTemplateService.getNotifyTemplateByCodeFromCache(eq(templateCode))).thenReturn(template);

        // invoke
        Long resultMessageId = notifySendService.sendSingleNotify(userId, userType, templateCode, templateParams);
        // assert
        assertNull(resultMessageId);
        verify(notifyTemplateService, never()).formatNotifyTemplateContent(anyString(), anyMap());
        verify(notifyMessageService, never()).createNotifyMessage(anyLong(), anyInt(), any(), anyString(), anyMap());
    }

    @Test
    public void testCheckMailTemplateValid_notExists() {
        // prepare parameters
        String templateCode = randomString();
        // mock the method

        // invoke, and assert exception
        assertServiceException(() -> notifySendService.validateNotifyTemplate(templateCode),
                NOTICE_NOT_FOUND);
    }

    @Test
    public void testCheckTemplateParams_paramMiss() {
        // prepare parameters
        NotifyTemplateEntity template = randomPojo(NotifyTemplateEntity.class,
                o -> o.setParams(Lists.newArrayList("code")));
        Map<String, Object> templateParams = new HashMap<>();
        // mock the method

        // invoke, and assert exception
        assertServiceException(() -> notifySendService.validateTemplateParams(template, templateParams),
                NOTIFY_SEND_TEMPLATE_PARAM_MISS, "code");
    }

    @Test
    public void testSendBatchNotify() {
        // prepare parameters
        // mock the method

        // invoke
        UnsupportedOperationException exception = Assertions.assertThrows(
                UnsupportedOperationException.class,
                () -> notifySendService.sendBatchNotify(null, null, null, null, null)
        );
        // assert
        assertEquals("temporarily not supported this operation, if interested can implement this feature!", exception.getMessage());
    }

}
