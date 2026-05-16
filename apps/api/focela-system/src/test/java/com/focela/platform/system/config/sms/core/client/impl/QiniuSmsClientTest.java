package com.focela.platform.system.config.sms.client.impl;

import com.focela.platform.common.core.KeyValue;
import com.focela.platform.common.utils.http.HttpUtils;
import com.focela.platform.test.core.support.BaseMockitoUnitTest;
import com.focela.platform.system.config.sms.client.dto.SmsReceiveRpcResponse;
import com.focela.platform.system.config.sms.client.dto.SmsSendRpcResponse;
import com.focela.platform.system.config.sms.client.dto.SmsTemplateRpcResponse;
import com.focela.platform.system.config.sms.enums.SmsTemplateAuditStatusEnum;
import com.focela.platform.system.config.sms.property.SmsChannelProperties;
import com.google.common.collect.Lists;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static com.focela.platform.test.core.utils.RandomUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

/**
 * {@link QiniuSmsClient}  unit test
 */
public class QiniuSmsClientTest extends BaseMockitoUnitTest {

    private final SmsChannelProperties properties = new SmsChannelProperties()
            .setApiKey(randomString())// random apiKey to avoid build errors
            .setApiSecret(randomString()) // random apiSecret to avoid build errors
            .setSignature("Focelasource");

    @InjectMocks
    private QiniuSmsClient smsClient = new QiniuSmsClient(properties);

    @Test
    public void testDoSendSms_success() throws Throwable {
        try (MockedStatic<HttpUtils> httpUtilsMockedStatic = mockStatic(HttpUtils.class)) {
            // prepare parameters
            Long sendLogId = randomLongId();
            String mobile = randomString();
            String apiTemplateId = randomString() + " " + randomString();
            List<KeyValue<String, Object>> templateParams = Lists.newArrayList(
                    new KeyValue<>("1", 1234), new KeyValue<>("2", "login"));
            // mock the method
            httpUtilsMockedStatic.when(() -> HttpUtils.post(anyString(), anyMap(), anyString()))
                    .thenReturn("{\"message_id\":\"17245678901\"}");
            // invoke
            SmsSendRpcResponse result = smsClient.sendSms(sendLogId, mobile,
                    apiTemplateId, templateParams);
            // assert
            assertTrue(result.getSuccess());
            assertEquals("17245678901", result.getSerialNo());
        }
    }

    @Test
    public void testDoSendSms_fail() throws Throwable {
        try (MockedStatic<HttpUtils> httpUtilsMockedStatic = mockStatic(HttpUtils.class)) {
            // prepare parameters
            Long sendLogId = randomLongId();
            String mobile = randomString();
            String apiTemplateId = randomString() + " " + randomString();
            List<KeyValue<String, Object>> templateParams = Lists.newArrayList(
                    new KeyValue<>("1", 1234), new KeyValue<>("2", "login"));
            // mock the method
            httpUtilsMockedStatic.when(() -> HttpUtils.post(anyString(), anyMap(), anyString()))
                    .thenReturn("{\"error\":\"BadToken\",\"message\":\"Your authorization token is invalid\",\"request_id\":\"etziWcJFo1C8Ne8X\"}");
            // invoke
            SmsSendRpcResponse result = smsClient.sendSms(sendLogId, mobile,
                    apiTemplateId, templateParams);
            // assert
            assertFalse(result.getSuccess());
            assertEquals("BadToken", result.getApiCode());
            assertEquals("Your authorization token is invalid", result.getApiMsg());
            assertEquals("etziWcJFo1C8Ne8X", result.getApiRequestId());
        }
    }

    @Test
    public void testGetSmsTemplate() throws Throwable {
        try (MockedStatic<HttpUtils> httpUtilsMockedStatic = mockStatic(HttpUtils.class)) {
            // prepare parameters
            String apiTemplateId = randomString();
            // mock the method
            httpUtilsMockedStatic.when(() -> HttpUtils.get(anyString(), anyMap()))
                    .thenReturn("{\"audit_status\":\"passed\",\"created_at\":1724231187,\"description\":\"\",\"disable_broadcast\":false,\"disable_broadcast_reason\":\"\",\"disable_reason\":\"\",\"disabled\":false,\"id\":\"1826184073773596672\",\"is_oversea\":false,\"name\":\"dd\",\"parameters\":[\"code\"],\"reject_reason\":\"\",\"signature_id\":\"1826099896017498112\",\"signature_text\":\"focela\",\"template\":\"Your verification code is: ${code}\",\"type\":\"verification\",\"uid\":1383022432,\"updated_at\":1724288561,\"variable_count\":0}");
            // invoke
            SmsTemplateRpcResponse result = smsClient.getSmsTemplate(apiTemplateId);
            // assert
            assertEquals("1826184073773596672", result.getId());
            assertEquals("Your verification code is: ${code}", result.getContent());
            assertEquals(SmsTemplateAuditStatusEnum.SUCCESS.getStatus(), result.getAuditStatus());
            assertEquals("", result.getAuditReason());
        }
    }

    @Test
    public void testParseSmsReceiveStatus() {
        // prepare parameters
        long deliveredAt = 1724591666L;
        String text = "{\"items\":[{\"mobile\":\"18881234567\",\"message_id\":\"10135515063508004167\",\"status\":\"DELIVRD\",\"delivrd_at\":" + deliveredAt + ",\"error\":\"DELIVRD\",\"seq\":\"123\"}]}";
        LocalDateTime expectedReceiveTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(deliveredAt), ZoneId.systemDefault());
        // invoke
        List<SmsReceiveRpcResponse> statuses = smsClient.parseSmsReceiveStatus(text);
        // assert
        assertEquals(1, statuses.size());
        SmsReceiveRpcResponse status = statuses.get(0);
        assertTrue(status.getSuccess());
        assertEquals("DELIVRD", status.getErrorMsg());
        assertEquals(expectedReceiveTime, status.getReceiveTime());
        assertEquals("18881234567", status.getMobile());
        assertEquals("10135515063508004167", status.getSerialNo());
        assertEquals(123, status.getLogId());
    }

    @Test
    public void testConvertSmsTemplateAuditStatus() {
        assertEquals(SmsTemplateAuditStatusEnum.SUCCESS.getStatus(),
                smsClient.convertSmsTemplateAuditStatus("passed"));
        assertEquals(SmsTemplateAuditStatusEnum.CHECKING.getStatus(),
                smsClient.convertSmsTemplateAuditStatus("reviewing"));
        assertEquals(SmsTemplateAuditStatusEnum.FAIL.getStatus(),
                smsClient.convertSmsTemplateAuditStatus("rejected"));
        assertThrows(IllegalArgumentException.class, () -> smsClient.convertSmsTemplateAuditStatus("unknown"),
                "unknown audit status(3)");
    }
}