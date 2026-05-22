package com.focela.platform.system.config.sms.client.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.digest.HmacAlgorithm;
import cn.hutool.http.HttpUtil;
import com.focela.platform.common.core.KeyValue;
import com.focela.platform.common.utils.collection.MapUtils;
import com.focela.platform.common.utils.json.JsonUtils;
import com.focela.platform.system.config.sms.client.response.SmsReceiveRpcResponse;
import com.focela.platform.system.config.sms.client.response.SmsSendRpcResponse;
import com.focela.platform.system.config.sms.client.response.SmsTemplateRpcResponse;
import com.focela.platform.system.config.sms.enums.SmsTemplateAuditStatusEnum;
import com.focela.platform.system.config.sms.property.SmsChannelProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Debug SMS client implementation based on DingTalk WebHook
 *
 * To save costs, we use a DingTalk WebHook to simulate sending SMS, which is convenient for debugging.
 */
public class DebugDingTalkSmsClient extends AbstractSmsClient {

    public DebugDingTalkSmsClient(SmsChannelProperties properties) {
        super(properties);
        Assert.notEmpty(properties.getApiKey(), "apiKey must not be blank");
        Assert.notEmpty(properties.getApiSecret(), "apiSecret must not be blank");
    }

    @Override
    public SmsSendRpcResponse sendSms(Long sendLogId, String mobile,
                                  String apiTemplateId, List<KeyValue<String, Object>> templateParams) throws Throwable {
        // build the request
        String url = buildUrl("robot/send");
        Map<String, Object> params = new HashMap<>();
        params.put("msgtype", "text");
        String content = String.format("[mock SMS]\nmobile number: %s\nSMS log ID: %d\ntemplate parameters: %s",
                mobile, sendLogId, MapUtils.convertMap(templateParams));
        params.put("text", MapUtil.builder().put("content", content).build());
        // execute the request
        String responseText = HttpUtil.post(url, JsonUtils.toJsonString(params));
        // parse the result
        Map<?, ?> responseObj = JsonUtils.parseObject(responseText, Map.class);
        String errorCode = MapUtil.getStr(responseObj, "errcode");
        return new SmsSendRpcResponse().setSuccess(Objects.equals(errorCode, "0")).setSerialNo(StrUtil.uuid())
                .setApiCode(errorCode).setApiMsg(MapUtil.getStr(responseObj, "errorMsg"));
    }

    /**
     * Build the request URL
     *
     * See <a href="https://developers.dingtalk.com/document/app/custom-robot-access/title-nfv-794-g71">documentation</a>
     *
     * @param path request path
     * @return request URL
     */
    @SuppressWarnings("SameParameterValue")
    private String buildUrl(String path) {
        // generate timestamp
        long timestamp = System.currentTimeMillis();
        // generate sign
        String secret = properties.getApiSecret();
        String stringToSign = timestamp + "\n" + secret;
        byte[] signData = DigestUtil.hmac(HmacAlgorithm.HmacSHA256, StrUtil.bytes(secret)).digest(stringToSign);
        String sign = Base64.encode(signData);
        // build the final URL
        return String.format("https://oapi.dingtalk.com/%s?access_token=%s&timestamp=%d&sign=%s",
                path, properties.getApiKey(), timestamp, sign);
    }

    @Override
    public List<SmsReceiveRpcResponse> parseSmsReceiveStatus(String text) {
        throw new UnsupportedOperationException("mock SMS client, temporarily no need to parse callback");
    }

    @Override
    public SmsTemplateRpcResponse getSmsTemplate(String apiTemplateId) {
        return new SmsTemplateRpcResponse().setId(apiTemplateId).setContent("")
                .setAuditStatus(SmsTemplateAuditStatusEnum.SUCCESS.getStatus()).setAuditReason("");
    }

}
