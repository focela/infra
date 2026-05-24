package com.focela.platform.system.config.sms.client.impl;

import cn.hutool.core.collection.ListUtil;
import com.focela.platform.common.core.KeyValue;
import com.focela.platform.system.config.sms.client.SmsClient;
import com.focela.platform.system.config.sms.client.response.SmsSendRpcResponse;
import com.focela.platform.system.config.sms.client.response.SmsTemplateRpcResponse;
import com.focela.platform.system.config.sms.property.SmsChannelProperties;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Various {@link SmsClient} manual integration tests.
 */
@Tag("manual")
public class SmsClientManualIntegrationTest {

    private static String env(String key, String defaultValue) {
        return System.getenv().getOrDefault(key, defaultValue);
    }

    // ========== Aliyun ==========

    @Test
    @Disabled("Requires provider credentials and sends a real SMS message")
    public void aliyunSmsClient_getSmsTemplate_returnsTemplate() throws Throwable {
        SmsChannelProperties properties = new SmsChannelProperties()
                .setApiKey(System.getenv("SMS_ALIYUN_ACCESS_KEY"))
                .setApiSecret(System.getenv("SMS_ALIYUN_SECRET_KEY"));
        AliyunSmsClient client = new AliyunSmsClient(properties);
        // prepare parameters
        String apiTemplateId = env("SMS_ALIYUN_TEMPLATE_ID", "SMS_TEMPLATE_ID");
        // invoke
        SmsTemplateRpcResponse template = client.getSmsTemplate(apiTemplateId);
        // print result
        System.out.println(template);
    }

    @Test
    @Disabled("Requires provider credentials and sends a real SMS message")
    public void aliyunSmsClient_sendSms_returnsResponse() throws Throwable {
        SmsChannelProperties properties = new SmsChannelProperties()
                .setApiKey(System.getenv("SMS_ALIYUN_ACCESS_KEY"))
                .setApiSecret(System.getenv("SMS_ALIYUN_SECRET_KEY"))
                .setSignature(env("SMS_ALIYUN_SIGNATURE", "SMS_SIGNATURE"));
        AliyunSmsClient client = new AliyunSmsClient(properties);
        // prepare parameters
        Long sendLogId = System.currentTimeMillis();
        String mobile = env("SMS_TEST_MOBILE", "13800000000");
        String apiTemplateId = env("SMS_ALIYUN_TEMPLATE_ID", "SMS_TEMPLATE_ID");
        // invoke
        SmsSendRpcResponse sendResponse = client.sendSms(sendLogId, mobile, apiTemplateId, ListUtil.of(new KeyValue<>("code", "1024")));
        // print result
        System.out.println(sendResponse);
    }

    // ========== Tencent Cloud ==========

    @Test
    @Disabled("Requires provider credentials and sends a real SMS message")
    public void tencentSmsClient_sendSms_returnsResponse() throws Throwable {
        String sdkAppId = env("SMS_TENCENT_SDK_APP_ID", "SMS_SDK_APP_ID");
        SmsChannelProperties properties = new SmsChannelProperties()
                .setApiKey(System.getenv("SMS_TENCENT_ACCESS_KEY") + " " + sdkAppId)
                .setApiSecret(System.getenv("SMS_TENCENT_SECRET_KEY"))
                .setSignature(env("SMS_TENCENT_SIGNATURE", "SMS_SIGNATURE"));
        TencentSmsClient client = new TencentSmsClient(properties);
        // prepare parameters
        Long sendLogId = System.currentTimeMillis();
        String mobile = env("SMS_TEST_MOBILE", "13800000000");
        String apiTemplateId = env("SMS_TENCENT_TEMPLATE_ID", "SMS_TEMPLATE_ID");
        // invoke
        SmsSendRpcResponse sendResponse = client.sendSms(sendLogId, mobile, apiTemplateId, ListUtil.of(new KeyValue<>("code", "1024")));
        // print result
        System.out.println(sendResponse);
    }

    @Test
    @Disabled("Requires provider credentials and sends a real SMS message")
    public void tencentSmsClient_getSmsTemplate_returnsTemplate() throws Throwable {
        String sdkAppId = env("SMS_TENCENT_SDK_APP_ID", "SMS_SDK_APP_ID");
        SmsChannelProperties properties = new SmsChannelProperties()
                .setApiKey(System.getenv("SMS_TENCENT_ACCESS_KEY") + " " + sdkAppId)
                .setApiSecret(System.getenv("SMS_TENCENT_SECRET_KEY"))
                .setSignature(env("SMS_TENCENT_SIGNATURE", "SMS_SIGNATURE"));
        TencentSmsClient client = new TencentSmsClient(properties);
        // prepare parameters
        String apiTemplateId = env("SMS_TENCENT_TEMPLATE_ID", "SMS_TEMPLATE_ID");
        // invoke
        SmsTemplateRpcResponse template = client.getSmsTemplate(apiTemplateId);
        // print result
        System.out.println(template);
    }

    // ========== Huawei Cloud ==========

    @Test
    @Disabled("Requires provider credentials and sends a real SMS message")
    public void huaweiSmsClient_sendSms_returnsResponse() throws Throwable {
        String sender = env("SMS_HUAWEI_SENDER", "SMS_SENDER");
        SmsChannelProperties properties = new SmsChannelProperties()
                .setApiKey(System.getenv("SMS_HUAWEI_ACCESS_KEY") + " " + sender)
                .setApiSecret(System.getenv("SMS_HUAWEI_SECRET_KEY"))
                .setSignature(env("SMS_HUAWEI_SIGNATURE", "SMS_SIGNATURE"));
        HuaweiSmsClient client = new HuaweiSmsClient(properties);
        // prepare parameters
        Long sendLogId = System.currentTimeMillis();
        String mobile = env("SMS_TEST_MOBILE", "13800000000");
        String apiTemplateId = env("SMS_HUAWEI_TEMPLATE_ID", "SMS_TEMPLATE_ID");
        List<KeyValue<String, Object>> templateParams = ListUtil.of(new KeyValue<>("code", "1024"));
        // invoke
        SmsSendRpcResponse smsSendResponse = client.sendSms(sendLogId, mobile, apiTemplateId, templateParams);
        // print result
        System.out.println(smsSendResponse);
    }

    // ========== Qiniu Cloud ==========

    @Test
    @Disabled("Requires provider credentials and sends a real SMS message")
    public void qiniuSmsClient_sendSms_returnsResponse() throws Throwable {
        SmsChannelProperties properties = new SmsChannelProperties()
                .setApiKey(System.getenv("SMS_QINIU_ACCESS_KEY"))
                .setApiSecret(System.getenv("SMS_QINIU_SECRET_KEY"));
        QiniuSmsClient client = new QiniuSmsClient(properties);
        // prepare parameters
        Long sendLogId = System.currentTimeMillis();
        String mobile = env("SMS_TEST_MOBILE", "13800000000");
        String apiTemplateId = env("SMS_QINIU_TEMPLATE_ID", "SMS_TEMPLATE_ID");
        List<KeyValue<String, Object>> templateParams = ListUtil.of(new KeyValue<>("code", "1122"));
        // invoke
        SmsSendRpcResponse smsSendResponse = client.sendSms(sendLogId, mobile, apiTemplateId, templateParams);
        // print result
        System.out.println(smsSendResponse);
    }

    @Test
    @Disabled("Requires provider credentials and sends a real SMS message")
    public void qiniuSmsClient_getSmsTemplate_returnsTemplate() throws Throwable {
        SmsChannelProperties properties = new SmsChannelProperties()
                .setApiKey(System.getenv("SMS_QINIU_ACCESS_KEY"))
                .setApiSecret(System.getenv("SMS_QINIU_SECRET_KEY"));
        QiniuSmsClient client = new QiniuSmsClient(properties);
        // prepare parameters
        String apiTemplateId = env("SMS_QINIU_TEMPLATE_ID", "SMS_TEMPLATE_ID");
        // invoke
        SmsTemplateRpcResponse template = client.getSmsTemplate(apiTemplateId);
        // print result
        System.out.println(template);
    }
}
