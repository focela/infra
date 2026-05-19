package com.focela.platform.system.service.sms;

import cn.hutool.core.map.MapUtil;
import com.focela.platform.common.core.KeyValue;
import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.system.config.sms.client.SmsClient;
import com.focela.platform.system.config.sms.client.dto.SmsReceiveRpcResponse;
import com.focela.platform.system.config.sms.client.dto.SmsSendRpcResponse;
import com.focela.platform.test.core.support.BaseMockitoUnitTest;
import com.focela.platform.system.domain.entity.sms.SmsChannelEntity;
import com.focela.platform.system.domain.entity.sms.SmsTemplateEntity;
import com.focela.platform.system.domain.entity.user.UserEntity;
import com.focela.platform.system.mq.message.sms.SmsSendMessage;
import com.focela.platform.system.mq.producer.sms.SmsProducer;
import com.focela.platform.system.service.member.MemberService;
import com.focela.platform.system.service.user.UserService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.hutool.core.util.RandomUtil.randomEle;
import static com.focela.platform.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.test.core.utils.RandomUtils.*;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class DefaultSmsSendServiceTest extends BaseMockitoUnitTest {

    @InjectMocks
    private DefaultSmsSendService smsSendService;

    @Mock
    private UserService adminUserService;
    @Mock
    private MemberService memberService;
    @Mock
    private SmsChannelService smsChannelService;
    @Mock
    private SmsTemplateService smsTemplateService;
    @Mock
    private SmsLogService smsLogService;
    @Mock
    private SmsProducer smsProducer;

    @Test
    public void testSendSingleSmsToAdmin() {
        // prepare parameters
        Long userId = randomLongId();
        String templateCode = randomString();
        Map<String, Object> templateParams = MapUtil.<String, Object>builder().put("code", "1234")
                .put("op", "login").build();
        // mock adminUserService  method
        UserEntity user = randomPojo(UserEntity.class, o -> o.setMobile("15601691300"));
        when(adminUserService.getUser(eq(userId))).thenReturn(user);

        // mock SmsTemplateService  method
        SmsTemplateEntity template = randomPojo(SmsTemplateEntity.class, o -> {
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
            o.setContent("verification code is {code}, operation is{op}");
            o.setParams(Lists.newArrayList("code", "op"));
        });
        when(smsTemplateService.getSmsTemplateByCodeFromCache(eq(templateCode))).thenReturn(template);
        String content = randomString();
        when(smsTemplateService.formatSmsTemplateContent(eq(template.getContent()), eq(templateParams)))
                .thenReturn(content);
        // mock SmsChannelService  method
        SmsChannelEntity smsChannel = randomPojo(SmsChannelEntity.class, o -> o.setStatus(CommonStatusEnum.ENABLE.getStatus()));
        when(smsChannelService.getSmsChannel(eq(template.getChannelId()))).thenReturn(smsChannel);
        // mock SmsLogService  method
        Long smsLogId = randomLongId();
        when(smsLogService.createSmsLog(eq(user.getMobile()), eq(userId), eq(UserTypeEnum.ADMIN.getValue()), eq(Boolean.TRUE), eq(template),
                eq(content), eq(templateParams))).thenReturn(smsLogId);

        // invoke
        Long resultSmsLogId = smsSendService.sendSingleSmsToAdmin(null, userId, templateCode, templateParams);
        // assert
        assertEquals(smsLogId, resultSmsLogId);
        // assert call
        verify(smsProducer).sendSmsSendMessage(eq(smsLogId), eq(user.getMobile()),
                eq(template.getChannelId()), eq(template.getApiTemplateId()),
                eq(Lists.newArrayList(new KeyValue<>("code", "1234"), new KeyValue<>("op", "login"))));
    }

    @Test
    public void testSendSingleSmsToUser() {
        // prepare parameters
        Long userId = randomLongId();
        String templateCode = randomString();
        Map<String, Object> templateParams = MapUtil.<String, Object>builder().put("code", "1234")
                .put("op", "login").build();
        // mock memberService  method
        String mobile = "15601691300";
        when(memberService.getMemberUserMobile(eq(userId))).thenReturn(mobile);

        // mock SmsTemplateService  method
        SmsTemplateEntity template = randomPojo(SmsTemplateEntity.class, o -> {
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
            o.setContent("verification code is {code}, operation is{op}");
            o.setParams(Lists.newArrayList("code", "op"));
        });
        when(smsTemplateService.getSmsTemplateByCodeFromCache(eq(templateCode))).thenReturn(template);
        String content = randomString();
        when(smsTemplateService.formatSmsTemplateContent(eq(template.getContent()), eq(templateParams)))
                .thenReturn(content);
        // mock SmsChannelService  method
        SmsChannelEntity smsChannel = randomPojo(SmsChannelEntity.class, o -> o.setStatus(CommonStatusEnum.ENABLE.getStatus()));
        when(smsChannelService.getSmsChannel(eq(template.getChannelId()))).thenReturn(smsChannel);
        // mock SmsLogService  method
        Long smsLogId = randomLongId();
        when(smsLogService.createSmsLog(eq(mobile), eq(userId), eq(UserTypeEnum.MEMBER.getValue()), eq(Boolean.TRUE), eq(template),
                eq(content), eq(templateParams))).thenReturn(smsLogId);

        // invoke
        Long resultSmsLogId = smsSendService.sendSingleSmsToMember(null, userId, templateCode, templateParams);
        // assert
        assertEquals(smsLogId, resultSmsLogId);
        // assert call
        verify(smsProducer).sendSmsSendMessage(eq(smsLogId), eq(mobile),
                eq(template.getChannelId()), eq(template.getApiTemplateId()),
                eq(Lists.newArrayList(new KeyValue<>("code", "1234"), new KeyValue<>("op", "login"))));
    }

    /**
     * send succeeds when SMS template is enabled
     */
    @Test
    public void testSendSingleSms_successWhenSmsTemplateEnable() {
        // prepare parameters
        String mobile = randomString();
        Long userId = randomLongId();
        Integer userType = randomEle(UserTypeEnum.values()).getValue();
        String templateCode = randomString();
        Map<String, Object> templateParams = MapUtil.<String, Object>builder().put("code", "1234")
                .put("op", "login").build();
        // mock SmsTemplateService  method
        SmsTemplateEntity template = randomPojo(SmsTemplateEntity.class, o -> {
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
            o.setContent("verification code is {code}, operation is{op}");
            o.setParams(Lists.newArrayList("code", "op"));
        });
        when(smsTemplateService.getSmsTemplateByCodeFromCache(eq(templateCode))).thenReturn(template);
        String content = randomString();
        when(smsTemplateService.formatSmsTemplateContent(eq(template.getContent()), eq(templateParams)))
                .thenReturn(content);
        // mock SmsChannelService  method
        SmsChannelEntity smsChannel = randomPojo(SmsChannelEntity.class, o -> o.setStatus(CommonStatusEnum.ENABLE.getStatus()));
        when(smsChannelService.getSmsChannel(eq(template.getChannelId()))).thenReturn(smsChannel);
        // mock SmsLogService  method
        Long smsLogId = randomLongId();
        when(smsLogService.createSmsLog(eq(mobile), eq(userId), eq(userType), eq(Boolean.TRUE), eq(template),
                eq(content), eq(templateParams))).thenReturn(smsLogId);

        // invoke
        Long resultSmsLogId = smsSendService.sendSingleSms(mobile, userId, userType, templateCode, templateParams);
        // assert
        assertEquals(smsLogId, resultSmsLogId);
        // assert call
        verify(smsProducer).sendSmsSendMessage(eq(smsLogId), eq(mobile),
                eq(template.getChannelId()), eq(template.getApiTemplateId()),
                eq(Lists.newArrayList(new KeyValue<>("code", "1234"), new KeyValue<>("op", "login"))));
    }

    /**
     * send succeeds when SMS template is disabled
     */
    @Test
    public void testSendSingleSms_successWhenSmsTemplateDisable() {
        // prepare parameters
        String mobile = randomString();
        Long userId = randomLongId();
        Integer userType = randomEle(UserTypeEnum.values()).getValue();
        String templateCode = randomString();
        Map<String, Object> templateParams = MapUtil.<String, Object>builder().put("code", "1234")
                .put("op", "login").build();
        // mock SmsTemplateService  method
        SmsTemplateEntity template = randomPojo(SmsTemplateEntity.class, o -> {
            o.setStatus(CommonStatusEnum.DISABLE.getStatus());
            o.setContent("verification code is {code}, operation is{op}");
            o.setParams(Lists.newArrayList("code", "op"));
        });
        when(smsTemplateService.getSmsTemplateByCodeFromCache(eq(templateCode))).thenReturn(template);
        String content = randomString();
        when(smsTemplateService.formatSmsTemplateContent(eq(template.getContent()), eq(templateParams)))
                .thenReturn(content);
        // mock SmsChannelService  method
        SmsChannelEntity smsChannel = randomPojo(SmsChannelEntity.class, o -> o.setStatus(CommonStatusEnum.ENABLE.getStatus()));
        when(smsChannelService.getSmsChannel(eq(template.getChannelId()))).thenReturn(smsChannel);
        // mock SmsLogService  method
        Long smsLogId = randomLongId();
        when(smsLogService.createSmsLog(eq(mobile), eq(userId), eq(userType), eq(Boolean.FALSE), eq(template),
                eq(content), eq(templateParams))).thenReturn(smsLogId);

        // invoke
        Long resultSmsLogId = smsSendService.sendSingleSms(mobile, userId, userType, templateCode, templateParams);
        // assert
        assertEquals(smsLogId, resultSmsLogId);
        // assert call
        verify(smsProducer, times(0)).sendSmsSendMessage(anyLong(), anyString(),
                anyLong(), any(), anyList());
    }

    @Test
    public void testCheckSmsTemplateValid_notExists() {
        // prepare parameters
        String templateCode = randomString();
        // mock the method

        // invoke, and assert exception
        assertServiceException(() -> smsSendService.validateSmsTemplate(templateCode),
                SMS_SEND_TEMPLATE_NOT_EXISTS);
    }

    @Test
    public void testBuildTemplateParams_paramMiss() {
        // prepare parameters
        SmsTemplateEntity template = randomPojo(SmsTemplateEntity.class,
                o -> o.setParams(Lists.newArrayList("code")));
        Map<String, Object> templateParams = new HashMap<>();
        // mock the method

        // invoke, and assert exception
        assertServiceException(() -> smsSendService.buildTemplateParams(template, templateParams),
                SMS_SEND_MOBILE_TEMPLATE_PARAM_MISS, "code");
    }

    @Test
    public void testCheckMobile_notExists() {
        // prepare parameters
        // mock the method

        // invoke, and assert exception
        assertServiceException(() -> smsSendService.validateMobile(null),
                SMS_SEND_MOBILE_NOT_EXISTS);
    }

    @Test
    public void testSendBatchNotify() {
        // prepare parameters
        // mock the method

        // invoke
        UnsupportedOperationException exception = Assertions.assertThrows(
                UnsupportedOperationException.class,
                () -> smsSendService.sendBatchSms(null, null, null, null, null)
        );
        // assert
        assertEquals("temporarily not supported this operation, if interested can implement this feature!", exception.getMessage());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDoSendSms() throws Throwable {
        // prepare parameters
        SmsSendMessage message = randomPojo(SmsSendMessage.class);
        // mock SmsClientFactory  method
        SmsClient smsClient = spy(SmsClient.class);
        when(smsChannelService.getSmsClient(eq(message.getChannelId()))).thenReturn(smsClient);
        // mock SmsClient  method
        SmsSendRpcResponse sendResult = randomPojo(SmsSendRpcResponse.class);
        when(smsClient.sendSms(eq(message.getLogId()), eq(message.getMobile()), eq(message.getApiTemplateId()),
                eq(message.getTemplateParams()))).thenReturn(sendResult);

        // invoke
        smsSendService.doSendSms(message);
        // assert
        verify(smsLogService).updateSmsSendResult(eq(message.getLogId()),
                eq(sendResult.getSuccess()), eq(sendResult.getApiCode()),
                eq(sendResult.getApiMsg()), eq(sendResult.getApiRequestId()), eq(sendResult.getSerialNo()));
    }

    @Test
    public void testReceiveSmsStatus() throws Throwable {
        // prepare parameters
        String channelCode = randomString();
        String text = randomString();
        // mock SmsClientFactory  method
        SmsClient smsClient = spy(SmsClient.class);
        when(smsChannelService.getSmsClient(eq(channelCode))).thenReturn(smsClient);
        // mock SmsClient  method
        List<SmsReceiveRpcResponse> receiveResults = randomPojoList(SmsReceiveRpcResponse.class);

        // invoke
        smsSendService.receiveSmsStatus(channelCode, text);
        // assert
        receiveResults.forEach(result -> smsLogService.updateSmsReceiveResult(eq(result.getLogId()), eq(result.getSerialNo()), eq(result.getSuccess()),
                eq(result.getReceiveTime()), eq(result.getErrorCode()), eq(result.getErrorCode())));
    }

}
