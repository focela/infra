package com.focela.platform.system.config.sms.client.impl;

import com.focela.platform.common.core.KeyValue;
import com.focela.platform.common.utils.http.HttpUtils;
import com.focela.platform.test.core.support.BaseMockitoUnitTest;
import com.focela.platform.system.config.sms.client.response.SmsReceiveRpcResponse;
import com.focela.platform.system.config.sms.client.response.SmsSendRpcResponse;
import com.focela.platform.system.config.sms.client.response.SmsTemplateRpcResponse;
import com.focela.platform.system.config.sms.enums.SmsTemplateAuditStatusEnum;
import com.focela.platform.system.config.sms.property.SmsChannelProperties;
import com.google.common.collect.Lists;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;

import java.time.LocalDateTime;
import java.util.List;

import static com.focela.platform.test.core.utils.RandomUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

/**
 * {@link TencentSmsClient}  unit test
 */
public class TencentSmsClientTest extends BaseMockitoUnitTest {

    private final SmsChannelProperties properties = new SmsChannelProperties()
            .setApiKey(randomString() + " " + randomString()) // random apiKey to avoid build errors
            .setApiSecret(randomString()) // random apiSecret to avoid build errors
            .setSignature("Focelasource");

    @InjectMocks
    private TencentSmsClient smsClient = new TencentSmsClient(properties);

    @Test
    public void sendSms_success_returnsProviderResponse() throws Throwable {
        try (MockedStatic<HttpUtils> httpUtilsMockedStatic = mockStatic(HttpUtils.class)) {
            // prepare parameters
            Long sendLogId = randomLongId();
            String mobile = randomString();
            String apiTemplateId = randomString();
            List<KeyValue<String, Object>> templateParams = Lists.newArrayList(
                    new KeyValue<>("1", 1234), new KeyValue<>("2", "login"));
            // mock the method
            httpUtilsMockedStatic.when(() -> HttpUtils.post(anyString(), anyMap(), anyString()))
                    .thenReturn("{\n" +
                                    "    \"Response\": {\n" +
                                    "        \"SendStatusSet\": [\n" +
                                    "            {\n" +
                                    "                \"SerialNo\": \"5000:1045710669157053657849499619\",\n" +
                                    "                \"PhoneNumber\": \"+8618511122233\",\n" +
                                    "                \"Fee\": 1,\n" +
                                    "                \"SessionContext\": \"test\",\n" +
                                    "                \"Code\": \"Ok\",\n" +
                                    "                \"Message\": \"send success\",\n" +
                                    "                \"IsoCode\": \"CN\"\n" +
                                    "            },\n" +
                                    "        ],\n" +
                                    "        \"RequestId\": \"a0aabda6-cf91-4f3e-a81f-9198114a2279\"\n" +
                                    "    }\n" +
                                    "}");

            // invoke
            SmsSendRpcResponse result = smsClient.sendSms(sendLogId, mobile,
                    apiTemplateId, templateParams);
            // assert
            assertTrue(result.getSuccess());
            assertEquals("5000:1045710669157053657849499619", result.getSerialNo());
            assertEquals("a0aabda6-cf91-4f3e-a81f-9198114a2279", result.getApiRequestId());
            assertEquals("send success", result.getApiMsg());
        }
    }

    @Test
    public void sendSms_providerStatusError_returnsFailure() throws Throwable {
        try (MockedStatic<HttpUtils> httpUtilsMockedStatic = mockStatic(HttpUtils.class)) {
            // prepare parameters
            Long sendLogId = randomLongId();
            String mobile = randomString();
            String apiTemplateId = randomString();
            List<KeyValue<String, Object>> templateParams = Lists.newArrayList(
                    new KeyValue<>("1", 1234), new KeyValue<>("2", "login"));

            // mock the method
            httpUtilsMockedStatic.when(() -> HttpUtils.post(anyString(), anyMap(), anyString()))
                    .thenReturn("{\n" +
                                    "    \"Response\": {\n" +
                                    "        \"SendStatusSet\": [\n" +
                                    "            {\n" +
                                    "                \"SerialNo\": \"5000:1045710669157053657849499619\",\n" +
                                    "                \"PhoneNumber\": \"+8618511122233\",\n" +
                                    "                \"Fee\": 1,\n" +
                                    "                \"SessionContext\": \"test\",\n" +
                                    "                \"Code\": \"ERROR\",\n" +
                                    "                \"Message\": \"send success\",\n" +
                                    "                \"IsoCode\": \"CN\"\n" +
                                    "            },\n" +
                                    "        ],\n" +
                                    "        \"RequestId\": \"a0aabda6-cf91-4f3e-a81f-9198114a2279\"\n" +
                                    "    }\n" +
                                    "}");

            // invoke
            SmsSendRpcResponse result = smsClient.sendSms(sendLogId, mobile,
                    apiTemplateId, templateParams);
            // assert
            assertFalse(result.getSuccess());
            assertEquals("5000:1045710669157053657849499619", result.getSerialNo());
            assertEquals("a0aabda6-cf91-4f3e-a81f-9198114a2279", result.getApiRequestId());
            assertEquals("send success", result.getApiMsg());
        }
    }

    @Test
    public void sendSms_authFailure_returnsFailure() throws Throwable {
        try (MockedStatic<HttpUtils> httpUtilsMockedStatic = mockStatic(HttpUtils.class)) {
            // prepare parameters
            Long sendLogId = randomLongId();
            String mobile = randomString();
            String apiTemplateId = randomString();
            List<KeyValue<String, Object>> templateParams = Lists.newArrayList(
                    new KeyValue<>("1", 1234), new KeyValue<>("2", "login"));

            // mock the method
            httpUtilsMockedStatic.when(() -> HttpUtils.post(anyString(), anyMap(), anyString()))
                    .thenReturn("{\"Response\":{\"Error\":{\"Code\":\"AuthFailure.SecretIdNotFound\",\"Message\":\"The SecretId is not found, please ensure that your SecretId is correct.\"},\"RequestId\":\"2a88f82a-261c-4ac6-9fa9-c7d01aaa486a\"}}");

            // invoke
            SmsSendRpcResponse result = smsClient.sendSms(sendLogId, mobile,
                    apiTemplateId, templateParams);
            // assert
            assertFalse(result.getSuccess());
            assertEquals("2a88f82a-261c-4ac6-9fa9-c7d01aaa486a", result.getApiRequestId());
            assertEquals("AuthFailure.SecretIdNotFound", result.getApiCode());
            assertEquals("The SecretId is not found, please ensure that your SecretId is correct.", result.getApiMsg());
        }
    }

    @Test
    public void parseSmsReceiveStatus_validPayload_returnsStatus() {
        // prepare parameters
        String text = "[\n" +
                "    {\n" +
                "        \"user_receive_time\": \"2015-10-17 08:03:04\",\n" +
                "        \"nationcode\": \"86\",\n" +
                "        \"mobile\": \"13900000001\",\n" +
                "        \"report_status\": \"SUCCESS\",\n" +
                "        \"errmsg\": \"DELIVRD\",\n" +
                "        \"description\": \"SMS delivered to user successfully\",\n" +
                "        \"sid\": \"12345\",\n" +
                "        \"ext\": {\"logId\":\"67890\"}\n" +
                "    }\n" +
                "]";

        // invoke
        List<SmsReceiveRpcResponse> statuses = smsClient.parseSmsReceiveStatus(text);
        // assert
        assertEquals(1, statuses.size());
        assertTrue(statuses.get(0).getSuccess());
        assertEquals("DELIVRD", statuses.get(0).getErrorCode());
        assertEquals("13900000001", statuses.get(0).getMobile());
        assertEquals(LocalDateTime.of(2015, 10, 17, 8, 3, 4), statuses.get(0).getReceiveTime());
        assertEquals("12345", statuses.get(0).getSerialNo());
    }

    @Test
    public void getSmsTemplate_success_returnsTemplate() throws Throwable {
        try (MockedStatic<HttpUtils> httpUtilsMockedStatic = mockStatic(HttpUtils.class)) {
            // prepare parameters
            String apiTemplateId = "1122";

            // mock the method
            httpUtilsMockedStatic.when(() -> HttpUtils.post(anyString(), anyMap(), anyString()))
                    .thenReturn("{     \"Response\": {\n" +
                            "        \"DescribeTemplateStatusSet\": [\n" +
                            "            {\n" +
                            "                \"TemplateName\": \"verification code\",\n" +
                            "                \"TemplateId\": 1122,\n" +
                            "                \"International\": 0,\n" +
                            "                \"ReviewReply\": \"audit reason\",\n" +
                            "                \"CreateTime\": 1617379200,\n" +
                            "                \"TemplateContent\": \"Your verification code is{1}\",\n" +
                            "                \"StatusCode\": 0\n" +
                            "            },\n" +
                            "            \n" +
                            "        ],\n" +
                            "        \"RequestId\": \"f36e4f00-605e-49b1-ad0d-bfaba81c7325\"\n" +
                            "    }}");

            // invoke
            SmsTemplateRpcResponse result = smsClient.getSmsTemplate(apiTemplateId);
            // assert
            assertEquals("1122", result.getId());
            assertEquals("Your verification code is{1}", result.getContent());
            assertEquals(SmsTemplateAuditStatusEnum.SUCCESS.getStatus(), result.getAuditStatus());
            assertEquals("audit reason", result.getAuditReason());
        }
    }

    @Test
    public void convertSmsTemplateAuditStatus_knownProviderStatuses_returnsInternalStatuses() {
        assertEquals(SmsTemplateAuditStatusEnum.SUCCESS.getStatus(),
                smsClient.convertSmsTemplateAuditStatus(0));
        assertEquals(SmsTemplateAuditStatusEnum.CHECKING.getStatus(),
                smsClient.convertSmsTemplateAuditStatus(1));
        assertEquals(SmsTemplateAuditStatusEnum.FAIL.getStatus(),
                smsClient.convertSmsTemplateAuditStatus(-1));
        assertThrows(IllegalArgumentException.class, () -> smsClient.convertSmsTemplateAuditStatus(3),
                "unknown audit status(3)");
    }

}
