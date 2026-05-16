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
import org.mockito.stubbing.Answer;

import java.time.LocalDateTime;
import java.util.List;

import static com.focela.platform.test.core.utils.RandomUtils.randomLongId;
import static com.focela.platform.test.core.utils.RandomUtils.randomString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

/**
 * {@link com.focela.platform.system.config.sms.client.impl.AliyunSmsClient}  unit test
 */
public class AliyunSmsClientTest extends BaseMockitoUnitTest {

    private final SmsChannelProperties properties = new SmsChannelProperties()
            .setApiKey(randomString()) // random apiKey to avoid build errors
            .setApiSecret(randomString()) // random apiSecret to avoid build errors
            .setSignature("Focelasource");

    @InjectMocks
    private final AliyunSmsClient smsClient = new AliyunSmsClient(properties);

    @Test
    public void tesSendSms_success() throws Throwable {
        try (MockedStatic<HttpUtils> httpUtilsMockedStatic = mockStatic(HttpUtils.class)) {
            // prepare parameters
            Long sendLogId = randomLongId();
            String mobile = randomString();
            String apiTemplateId = randomString();
            List<KeyValue<String, Object>> templateParams = Lists.newArrayList(
                    new KeyValue<>("code", 1234), new KeyValue<>("op", "login"));
            // mock the method
            httpUtilsMockedStatic.when(() -> HttpUtils.post(anyString(), anyMap(), anyString()))
                    .thenReturn("{\"Message\":\"OK\",\"RequestId\":\"30067CE9-3710-5984-8881-909B21D8DB28\",\"Code\":\"OK\",\"BizId\":\"800025323183427988\"}");
            httpUtilsMockedStatic.when(() -> HttpUtils.encodeUtf8(anyString()))
                    .then((Answer<String>) invocationOnMock -> (String) invocationOnMock.getArguments()[0]);

            // invoke
            SmsSendRpcResponse result = smsClient.sendSms(sendLogId, mobile,
                    apiTemplateId, templateParams);
            // assert
            assertTrue(result.getSuccess());
            assertEquals("30067CE9-3710-5984-8881-909B21D8DB28", result.getApiRequestId());
            assertEquals("OK", result.getApiCode());
            assertEquals("OK", result.getApiMsg());
            assertEquals("800025323183427988", result.getSerialNo());
        }
    }

    @Test
    public void tesSendSms_fail() throws Throwable {
        try (MockedStatic<HttpUtils> httpUtilsMockedStatic = mockStatic(HttpUtils.class)) {
            // prepare parameters
            Long sendLogId = randomLongId();
            String mobile = randomString();
            String apiTemplateId = randomString();
            List<KeyValue<String, Object>> templateParams = Lists.newArrayList(
                    new KeyValue<>("code", 1234), new KeyValue<>("op", "login"));
            // mock the method
            httpUtilsMockedStatic.when(() -> HttpUtils.post(anyString(), anyMap(), anyString()))
                    .thenReturn("{\"Message\":\"Mobile number format is invalid\",\"RequestId\":\"B7700B8E-227E-5886-9564-26036172F01F\",\"Code\":\"isv.MOBILE_NUMBER_ILLEGAL\"}");
            httpUtilsMockedStatic.when(() -> HttpUtils.encodeUtf8(anyString()))
                    .then((Answer<String>) invocationOnMock -> (String) invocationOnMock.getArguments()[0]);

            // invoke
            SmsSendRpcResponse result = smsClient.sendSms(sendLogId, mobile, apiTemplateId, templateParams);
            // assert
            assertFalse(result.getSuccess());
            assertEquals("B7700B8E-227E-5886-9564-26036172F01F", result.getApiRequestId());
            assertEquals("isv.MOBILE_NUMBER_ILLEGAL", result.getApiCode());
            assertEquals("Mobile number format is invalid", result.getApiMsg());
            assertNull(result.getSerialNo());
        }
    }

    @Test
    public void testParseSmsReceiveStatus() {
        // prepare parameters
        String text = "[\n" +
                "  {\n" +
                "    \"phone_number\" : \"13900000001\",\n" +
                "    \"send_time\" : \"2017-01-01 11:12:13\",\n" +
                "    \"report_time\" : \"2017-02-02 22:23:24\",\n" +
                "    \"success\" : true,\n" +
                "    \"err_code\" : \"DELIVERED\",\n" +
                "    \"err_msg\" : \"user received successfully\",\n" +
                "    \"sms_size\" : \"1\",\n" +
                "    \"biz_id\" : \"12345\",\n" +
                "    \"out_id\" : \"67890\"\n" +
                "  }\n" +
                "]";
        // mock the method

        // invoke
        List<SmsReceiveRpcResponse> statuses = smsClient.parseSmsReceiveStatus(text);
        // assert
        assertEquals(1, statuses.size());
        assertTrue(statuses.get(0).getSuccess());
        assertEquals("DELIVERED", statuses.get(0).getErrorCode());
        assertEquals("user received successfully", statuses.get(0).getErrorMsg());
        assertEquals("13900000001", statuses.get(0).getMobile());
        assertEquals(LocalDateTime.of(2017, 2, 2, 22, 23, 24),
                statuses.get(0).getReceiveTime());
        assertEquals("12345", statuses.get(0).getSerialNo());
        assertEquals(67890L, statuses.get(0).getLogId());
    }

    @Test
    public void testGetSmsTemplate() throws Throwable {
        try (MockedStatic<HttpUtils> httpUtilsMockedStatic = mockStatic(HttpUtils.class)) {
            // prepare parameters
            String apiTemplateId = randomString();
            // mock the method
            httpUtilsMockedStatic.when(() -> HttpUtils.post(anyString(), anyMap(), anyString()))
                    .thenReturn("{\"TemplateCode\":\"SMS_207945135\",\"RequestId\":\"6F4CC077-29C8-5BA5-AB62-5FF95068A5AC\",\"Message\":\"OK\",\"TemplateContent\":\"Your verification code ${code}, This verification code is valid for 5 minutes. Do not share it with others!\",\"TemplateName\":\"Announcement\",\"TemplateType\":0,\"Code\":\"OK\",\"CreateDate\":\"2020-12-23 17:34:42\",\"Reason\":\"no audit reason\",\"TemplateStatus\":1}");
            httpUtilsMockedStatic.when(() -> HttpUtils.encodeUtf8(anyString()))
                    .then((Answer<String>) invocationOnMock -> (String) invocationOnMock.getArguments()[0]);

            // invoke
            SmsTemplateRpcResponse result = smsClient.getSmsTemplate(apiTemplateId);
            // assert
            assertEquals("SMS_207945135", result.getId());
            assertEquals("Your verification code ${code}, This verification code is valid for 5 minutes. Do not share it with others!", result.getContent());
            assertEquals(SmsTemplateAuditStatusEnum.SUCCESS.getStatus(), result.getAuditStatus());
            assertEquals("no audit reason", result.getAuditReason());
        }
    }

    @Test
    public void testConvertSmsTemplateAuditStatus() {
        assertEquals(SmsTemplateAuditStatusEnum.CHECKING.getStatus(),
                smsClient.convertSmsTemplateAuditStatus(0));
        assertEquals(SmsTemplateAuditStatusEnum.SUCCESS.getStatus(),
                smsClient.convertSmsTemplateAuditStatus(1));
        assertEquals(SmsTemplateAuditStatusEnum.FAIL.getStatus(),
                smsClient.convertSmsTemplateAuditStatus(2));
        assertThrows(IllegalArgumentException.class, () -> smsClient.convertSmsTemplateAuditStatus(3),
                "unknown audit status(3)");
    }

}
