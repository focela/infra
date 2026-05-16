package com.focela.platform.system.config.sms.client.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.focela.platform.common.core.KeyValue;
import com.focela.platform.common.utils.http.HttpUtils;
import com.focela.platform.common.utils.json.JsonUtils;
import com.focela.platform.system.config.sms.client.dto.SmsReceiveRpcResponse;
import com.focela.platform.system.config.sms.client.dto.SmsSendRpcResponse;
import com.focela.platform.system.config.sms.client.dto.SmsTemplateRpcResponse;
import com.focela.platform.system.config.sms.enums.SmsTemplateAuditStatusEnum;
import com.focela.platform.system.config.sms.property.SmsChannelProperties;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static cn.hutool.crypto.digest.DigestUtil.sha256Hex;
import static com.focela.platform.common.utils.collection.CollectionUtils.convertList;

/**
 * Huawei SMS client implementation
 *
 * @since 2024/6/02 11:55
 */
@Slf4j
public class HuaweiSmsClient extends AbstractSmsClient {

    private static final String URL = "https://smsapi.cn-north-4.myhuaweicloud.com:443/sms/batchSendSms/v1"; // app endpoint address + access URI
    private static final String HOST = "smsapi.cn-north-4.myhuaweicloud.com:443";
    private static final String SIGNEDHEADERS = "content-type;host;x-sdk-date";

    private static final String RESPONSE_CODE_SUCCESS = "000000";

    public HuaweiSmsClient(SmsChannelProperties properties) {
        super(properties);
        Assert.notEmpty(properties.getApiKey(), "apiKey must not be blank");
        Assert.notEmpty(properties.getApiSecret(), "apiSecret must not be blank");
        validateSender(properties);
    }

    /**
     * Validate Huawei Cloud's sender channel number
     *
     * Reason: sending SMS via Huawei Cloud requires an additional parameter sender.
     *
     * Solution: to avoid breaking the existing apiKey + apiSecret structure, the secretId is concatenated into the apiKey field with the format "secretId sdkAppId".
     *
     * @param properties config
     */
    private static void validateSender(SmsChannelProperties properties) {
        String combineKey = properties.getApiKey();
        Assert.notEmpty(combineKey, "apiKey must not be blank");
        String[] keys = combineKey.trim().split(" ");
        Assert.isTrue(keys.length == 2, "Huawei Cloud SMS apiKey config format is incorrect, please configure as [accessKeyId sender]");
    }

    private String getAccessKey() {
        return StrUtil.subBefore(properties.getApiKey(), " ", true);
    }

    private String getSender() {
        return StrUtil.subAfter(properties.getApiKey(), " ", true);
    }

    @Override
    public SmsSendRpcResponse sendSms(Long sendLogId, String mobile, String apiTemplateId,
                                  List<KeyValue<String, Object>> templateParams) throws Throwable {
        StringBuilder requestBody = new StringBuilder();
        appendToBody(requestBody, "from=", getSender());
        appendToBody(requestBody, "&to=", mobile);
        appendToBody(requestBody, "&templateId=", apiTemplateId);
        appendToBody(requestBody, "&templateParas=", JsonUtils.toJsonString(
                convertList(templateParams, kv -> String.valueOf(kv.getValue()))));
        appendToBody(requestBody, "&statusCallback=", properties.getCallbackUrl());
        appendToBody(requestBody, "&extend=", String.valueOf(sendLogId));
        JSONObject response = request("/sms/batchSendSms/v1/", "POST", requestBody.toString());

        // 2. parse the response
        if (!response.containsKey("result")) { // e.g. incorrect key
            return new SmsSendRpcResponse().setSuccess(false)
                    .setApiCode(response.getStr("code"))
                    .setApiMsg(response.getStr("description"));
        }
        JSONObject sendResult = response.getJSONArray("result").getJSONObject(0);
        return new SmsSendRpcResponse().setSuccess(RESPONSE_CODE_SUCCESS.equals(response.getStr("code")))
                .setSerialNo(sendResult.getStr("smsMsgId")).setApiCode(sendResult.getStr("status"));
    }

    /**
     * Send a request to Huawei Cloud SMS
     *
     * @see <a href="https://support.huaweicloud.com/api-msgsms/sms_05_0046.html">Authentication</a>
     * @param uri         request URI
     * @param method      request method
     * @param requestBody request body
     * @return request result
     */
    private JSONObject request(String uri, String method, String requestBody) {
        // 1.1 request headers
        TreeMap<String, String> headers = new TreeMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        String sdkDate = FastDateFormat.getInstance("yyyyMMdd'T'HHmmss'Z'", TimeZone.getTimeZone("UTC")).format(new Date());
        headers.put("X-Sdk-Date", sdkDate);
        headers.put("host", HOST);

        // 1.2 build the signed headers
        String canonicalQueryString = ""; // query parameters are empty
        String canonicalHeaders = "content-type:application/x-www-form-urlencoded\n"
                + "host:"+ HOST +"\n" + "x-sdk-date:" + sdkDate + "\n";
        String canonicalRequest = method + "\n" + uri + "\n" + canonicalQueryString + "\n"
                + canonicalHeaders + "\n" + SIGNEDHEADERS + "\n" + sha256Hex(requestBody);
        String stringToSign = "SDK-HMAC-SHA256" + "\n" + sdkDate + "\n" + sha256Hex(canonicalRequest);
        String signature = SecureUtil.hmacSha256(properties.getApiSecret()).digestHex(stringToSign);  // compute the signature
        headers.put("Authorization", "SDK-HMAC-SHA256" + " " + "Access=" + getAccessKey()
                + ", " + "SignedHeaders=" + SIGNEDHEADERS + ", " + "Signature=" + signature);

        // 2. send the request
        String responseBody = HttpUtils.post(URL, headers, requestBody);
        return JSONUtil.parseObj(responseBody);
    }

    @Override
    public List<SmsReceiveRpcResponse> parseSmsReceiveStatus(String requestBody) {
        Map<String, String> params = HttpUtil.decodeParamMap(requestBody, StandardCharsets.UTF_8);
        // Field reference: https://support.huaweicloud.com/api-msgsms/sms_05_0003.html
        return ListUtil.of(new SmsReceiveRpcResponse()
                .setSuccess("DELIVRD".equals(params.get("status"))) // whether received successfully
                .setErrorCode(params.get("status")) // status report code
                .setErrorMsg(params.get("statusDesc"))
                .setMobile(params.get("to")) // mobile number
                .setReceiveTime(LocalDateTime.ofInstant(Instant.parse(params.get("updateTime")), ZoneId.of("UTC"))) // status report time
                .setSerialNo(params.get("smsMsgId")) // send serial number
                .setLogId(Long.valueOf(params.get("extend")))); // user serial number
    }

    @Override
    public SmsTemplateRpcResponse getSmsTemplate(String apiTemplateId) throws Throwable {
        // Huawei SMS template query and SMS sending use different key/secret pairs, which differs significantly from Aliyun/Tencent.
        // For now, template query validation is not implemented here.
        String[] strs = apiTemplateId.split(" ");
        Assert.isTrue(strs.length == 2, "incorrect format, expected: apiTemplateId sender");
        return new SmsTemplateRpcResponse().setId(apiTemplateId).setContent(null)
                .setAuditStatus(SmsTemplateAuditStatusEnum.SUCCESS.getStatus()).setAuditReason(null);
    }

    private static void appendToBody(StringBuilder body, String key, String value) {
        if (StrUtil.isNotEmpty(value)) {
            body.append(key).append(HttpUtils.encodeUtf8(value));
        }
    }

}
