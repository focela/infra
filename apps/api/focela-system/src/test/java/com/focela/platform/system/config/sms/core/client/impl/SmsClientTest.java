package com.focela.platform.system.config.sms.client.impl;

import cn.hutool.core.collection.ListUtil;
import com.focela.platform.common.core.KeyValue;
import com.focela.platform.system.config.sms.client.SmsClient;
import com.focela.platform.system.config.sms.client.dto.SmsSendRpcResponse;
import com.focela.platform.system.config.sms.client.dto.SmsTemplateRpcResponse;
import com.focela.platform.system.config.sms.property.SmsChannelProperties;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * various {@link SmsClient}  integration test
 */
public class SmsClientTest {

    // ========== Aliyun ==========

    @Test
    @Disabled
    public void testAliyunSmsClient_getSmsTemplate() throws Throwable {
        SmsChannelProperties properties = new SmsChannelProperties()
                .setApiKey(System.getenv("SMS_ALIYUN_ACCESS_KEY"))
                .setApiSecret(System.getenv("SMS_ALIYUN_SECRET_KEY"));
        AliyunSmsClient client = new AliyunSmsClient(properties);
        // prepare parameters
        String apiTemplateId = "SMS_207945135";
        // invoke
        SmsTemplateRpcResponse template = client.getSmsTemplate(apiTemplateId);
        // print result
        System.out.println(template);
    }

    @Test
    @Disabled
    public void testAliyunSmsClient_sendSms() throws Throwable {
        SmsChannelProperties properties = new SmsChannelProperties()
                .setApiKey(System.getenv("SMS_ALIYUN_ACCESS_KEY"))
                .setApiSecret(System.getenv("SMS_ALIYUN_SECRET_KEY"))
                .setSignature("Ballcat");
        AliyunSmsClient client = new AliyunSmsClient(properties);
        // prepare parameters
        Long sendLogId = System.currentTimeMillis();
        String mobile = "15601691323";
        String apiTemplateId = "SMS_207945135";
        // invoke
        SmsSendRpcResponse sendResponse = client.sendSms(sendLogId, mobile, apiTemplateId, ListUtil.of(new KeyValue<>("code", "1024")));
        // print result
        System.out.println(sendResponse);
    }

    // ========== Tencent Cloud ==========

    @Test
    @Disabled
    public void testTencentSmsClient_sendSms() throws Throwable {
        String sdkAppId = "1400500458";
        SmsChannelProperties properties = new SmsChannelProperties()
                .setApiKey(System.getenv("SMS_TENCENT_ACCESS_KEY") + " " + sdkAppId)
                .setApiSecret(System.getenv("SMS_TENCENT_SECRET_KEY"))
                .setSignature("Focelasource");
        TencentSmsClient client = new TencentSmsClient(properties);
        // prepare parameters
        Long sendLogId = System.currentTimeMillis();
        String mobile = "15601691323";
        String apiTemplateId = "358212";
        // invoke
        SmsSendRpcResponse sendResponse = client.sendSms(sendLogId, mobile, apiTemplateId, ListUtil.of(new KeyValue<>("code", "1024")));
        // print result
        System.out.println(sendResponse);
    }

    @Test
    @Disabled
    public void testTencentSmsClient_getSmsTemplate() throws Throwable {
        String sdkAppId = "1400500458";
        SmsChannelProperties properties = new SmsChannelProperties()
                .setApiKey(System.getenv("SMS_TENCENT_ACCESS_KEY") + " " + sdkAppId)
                .setApiSecret(System.getenv("SMS_TENCENT_SECRET_KEY"))
                .setSignature("Focelasource");
        TencentSmsClient client = new TencentSmsClient(properties);
        // prepare parameters
        String apiTemplateId = "358212";
        // invoke
        SmsTemplateRpcResponse template = client.getSmsTemplate(apiTemplateId);
        // print result
        System.out.println(template);
    }

    // ========== Huawei Cloud ==========

    @Test
    @Disabled
    public void testHuaweiSmsClient_sendSms() throws Throwable {
        String sender = "x8824060312575";
        SmsChannelProperties properties = new SmsChannelProperties()
                .setApiKey(System.getenv("SMS_HUAWEI_ACCESS_KEY") + " " + sender)
                .setApiSecret(System.getenv("SMS_HUAWEI_SECRET_KEY"))
                .setSignature("runpu");
        HuaweiSmsClient client = new HuaweiSmsClient(properties);
        // prepare parameters
        Long sendLogId = System.currentTimeMillis();
        String mobile = "17321315478";
        String apiTemplateId = "3644cdab863546a3b718d488659a99ef";
        List<KeyValue<String, Object>> templateParams = ListUtil.of(new KeyValue<>("code", "1024"));
        // invoke
        SmsSendRpcResponse smsSendResponse = client.sendSms(sendLogId, mobile, apiTemplateId, templateParams);
        // print result
        System.out.println(smsSendResponse);
    }

    // ========== Qiniu Cloud ==========

    @Test
    @Disabled
    public void testQiniuSmsClient_sendSms() throws Throwable {
        SmsChannelProperties properties = new SmsChannelProperties()
                .setApiKey("SMS_QINIU_ACCESS_KEY")
                .setApiSecret("SMS_QINIU_SECRET_KEY");
        QiniuSmsClient client = new QiniuSmsClient(properties);
        // prepare parameters
        Long sendLogId = System.currentTimeMillis();
        String mobile = "17321315478";
        String apiTemplateId = "3644cdab863546a3b718d488659a99ef";
        List<KeyValue<String, Object>> templateParams = ListUtil.of(new KeyValue<>("code", "1122"));
        // invoke
        SmsSendRpcResponse smsSendResponse = client.sendSms(sendLogId, mobile, apiTemplateId, templateParams);
        // print result
        System.out.println(smsSendResponse);
    }

    @Test
    @Disabled
    public void testQiniuSmsClient_getSmsTemplate() throws Throwable {
        SmsChannelProperties properties = new SmsChannelProperties()
                .setApiKey("SMS_QINIU_ACCESS_KEY")
                .setApiSecret("SMS_QINIU_SECRET_KEY");
        QiniuSmsClient client = new QiniuSmsClient(properties);
        // prepare parameters
        String apiTemplateId = "3644cdab863546a3b718d488659a99ef";
        // invoke
        SmsTemplateRpcResponse template = client.getSmsTemplate(apiTemplateId);
        // print result
        System.out.println(template);
    }
}

