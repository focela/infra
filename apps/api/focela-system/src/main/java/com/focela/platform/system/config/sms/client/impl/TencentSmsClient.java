package com.focela.platform.system.config.sms.client.impl;

import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.digest.HmacAlgorithm;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.focela.platform.common.core.KeyValue;
import com.focela.platform.common.utils.collection.ArrayUtils;
import com.focela.platform.common.utils.http.HttpUtils;
import com.focela.platform.system.config.sms.client.dto.SmsReceiveRpcResponse;
import com.focela.platform.system.config.sms.client.dto.SmsSendRpcResponse;
import com.focela.platform.system.config.sms.client.dto.SmsTemplateRpcResponse;
import com.focela.platform.system.config.sms.enums.SmsTemplateAuditStatusEnum;
import com.focela.platform.system.config.sms.property.SmsChannelProperties;
import com.google.common.annotations.VisibleForTesting;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.TreeMap;

import static cn.hutool.crypto.digest.DigestUtil.sha256Hex;
import static com.focela.platform.common.utils.collection.CollectionUtils.convertList;

/**
 * Tencent Cloud SMS client implementation
 *
 * See <a href="https://cloud.tencent.com/document/product/382/52077">documentation</a>
 */
public class TencentSmsClient extends AbstractSmsClient {

    private static final String SMS_API_HOST = "sms.tencentcloudapi.com";
    private static final String VERSION = "2021-01-11";
    private static final String REGION = "ap-guangzhou";

    /**
     * Code indicating a successful call
     */
    public static final String API_CODE_SUCCESS = "Ok";

    /**
     * Whether it is an international / HK-Macau-Taiwan SMS:
     *
     * 0: domestic SMS.
     * 1: international / HK-Macau-Taiwan SMS.
     */
    private static final long INTERNATIONAL_CHINA = 0L;

    public TencentSmsClient(SmsChannelProperties properties) {
        super(properties);
        Assert.notEmpty(properties.getApiSecret(), "apiSecret must not be blank");
        validateSdkAppId(properties);
    }

    /**
     * Validate Tencent Cloud's SDK AppId parameter
     *
     * Reason: sending SMS via Tencent Cloud requires an additional parameter sdkAppId.
     *
     * Solution: to avoid breaking the existing apiKey + apiSecret structure, the secretId is concatenated into the apiKey field with the format "secretId sdkAppId".
     *
     * @param properties config
     */
    private static void validateSdkAppId(SmsChannelProperties properties) {
        String combineKey = properties.getApiKey();
        Assert.notEmpty(combineKey, "apiKey must not be blank");
        String[] keys = combineKey.trim().split(" ");
        Assert.isTrue(keys.length == 2, "Tencent Cloud SMS apiKey config format is incorrect, please configure as [secretId sdkAppId]");
    }

    private String getSdkAppId() {
        return StrUtil.subAfter(properties.getApiKey(), " ", true);
    }

    private String getApiKey() {
        return StrUtil.subBefore(properties.getApiKey(), " ", true);
    }

    @Override
    public SmsSendRpcResponse sendSms(Long sendLogId, String mobile,
                                  String apiTemplateId, List<KeyValue<String, Object>> templateParams) throws Throwable {
        // 1. execute the request
        // Reference: https://cloud.tencent.com/document/product/382/55981
        TreeMap<String, Object> body = new TreeMap<>();
        body.put("PhoneNumberSet", new String[]{mobile});
        body.put("SmsSdkAppId", getSdkAppId());
        body.put("SignName", properties.getSignature());
        body.put("TemplateId", apiTemplateId);
        body.put("TemplateParamSet", ArrayUtils.toArray(templateParams, param -> String.valueOf(param.getValue())));
        JSONObject response = request("SendSms", body);

        // 2. parse the response
        JSONObject responseResult = response.getJSONObject("Response");
        JSONObject error = responseResult.getJSONObject("Error");
        if (error != null) {
            return new SmsSendRpcResponse().setSuccess(false)
                    .setApiRequestId(responseResult.getStr("RequestId"))
                    .setApiCode(error.getStr("Code"))
                    .setApiMsg(error.getStr("Message"));
        }
        JSONObject sendResult = responseResult.getJSONArray("SendStatusSet").getJSONObject(0);
        return new SmsSendRpcResponse().setSuccess(Objects.equals(API_CODE_SUCCESS, sendResult.getStr("Code")))
                .setApiRequestId(responseResult.getStr("RequestId"))
                .setSerialNo(sendResult.getStr("SerialNo"))
                .setApiMsg(sendResult.getStr("Message"));
    }

    @Override
    public List<SmsReceiveRpcResponse> parseSmsReceiveStatus(String text) {
        JSONArray statuses = JSONUtil.parseArray(text);
        // field reference
        return convertList(statuses, status -> {
            JSONObject statusObj = (JSONObject) status;
            return new SmsReceiveRpcResponse()
                    .setSuccess("SUCCESS".equals(statusObj.getStr("report_status"))) // whether received successfully
                    .setErrorCode(statusObj.getStr("errmsg")) // status report code
                    .setErrorMsg(statusObj.getStr("description")) // status report description
                    .setMobile(statusObj.getStr("mobile")) // mobile number
                    .setReceiveTime(statusObj.getLocalDateTime("user_receive_time", null)) // status report time
                    .setSerialNo(statusObj.getStr("sid")); // send serial number
        });
    }

    @Override
    public SmsTemplateRpcResponse getSmsTemplate(String apiTemplateId) throws Throwable {
        // 1. build the request
        // Reference: https://cloud.tencent.com/document/product/382/52067
        TreeMap<String, Object> body = new TreeMap<>();
        body.put("International", INTERNATIONAL_CHINA);
        body.put("TemplateIdSet", new Integer[]{Integer.valueOf(apiTemplateId)});
        JSONObject response = request("DescribeSmsTemplateList", body);

        // 2. parse the response
        JSONObject statusResult = response.getJSONObject("Response")
                .getJSONArray("DescribeTemplateStatusSet").getJSONObject(0);
        return new SmsTemplateRpcResponse().setId(apiTemplateId)
                .setContent(statusResult.get("TemplateContent").toString())
                .setAuditStatus(convertSmsTemplateAuditStatus(statusResult.getInt("StatusCode")))
                .setAuditReason(statusResult.get("ReviewReply").toString());
    }

    @VisibleForTesting
    Integer convertSmsTemplateAuditStatus(int templateStatus) {
        switch (templateStatus) {
            case 1: return SmsTemplateAuditStatusEnum.CHECKING.getStatus();
            case 0: return SmsTemplateAuditStatusEnum.SUCCESS.getStatus();
            case -1: return SmsTemplateAuditStatusEnum.FAIL.getStatus();
            default: throw new IllegalArgumentException(String.format("unknown approval status (%d)", templateStatus));
        }
    }

    /**
     * Send a request to Tencent Cloud SMS
     *
     * @see <a href="https://cloud.tencent.com/document/product/382/52072">Signature method v3</a>
     *
     * @param action name of the API to request
     * @param body   request parameters
     * @return request result
     */
    private JSONObject request(String action, TreeMap<String, Object> body) {
        // 1.1 request headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Host", SMS_API_HOST);
        headers.put("X-TC-Action", action);
        Date now = new Date();
        String nowStr = FastDateFormat.getInstance("yyyy-MM-dd", TimeZone.getTimeZone("UTC")).format(now);
        headers.put("X-TC-Timestamp", String.valueOf(now.getTime() / 1000));
        headers.put("X-TC-Version", VERSION);
        headers.put("X-TC-Region", REGION);

        // 1.2 build the signed headers
        String canonicalQueryString = "";
        String canonicalHeaders = "content-type:application/json; charset=utf-8\n"
                + "host:" + SMS_API_HOST + "\n" + "x-tc-action:" + action.toLowerCase() + "\n";
        String signedHeaders = "content-type;host;x-tc-action";
        String canonicalRequest = "POST" + "\n" + "/" + "\n" + canonicalQueryString + "\n" + canonicalHeaders + "\n"
                + signedHeaders + "\n" + sha256Hex(JSONUtil.toJsonStr(body));
        String credentialScope = nowStr + "/" + "sms" + "/" + "tc3_request";
        String stringToSign = "TC3-HMAC-SHA256" + "\n" + now.getTime() / 1000 + "\n" + credentialScope + "\n" +
                sha256Hex(canonicalRequest);
        byte[] secretService = hmac256(hmac256(("TC3" + properties.getApiSecret()).getBytes(StandardCharsets.UTF_8), nowStr), "sms");
        String signature = HexUtil.encodeHexStr(hmac256(hmac256(secretService, "tc3_request"), stringToSign));
        headers.put("Authorization", "TC3-HMAC-SHA256" + " " + "Credential=" + getApiKey() + "/" + credentialScope + ", "
                + "SignedHeaders=" + signedHeaders + ", " + "Signature=" + signature);

        // 2. send the request
        String responseBody = HttpUtils.post("https://" + SMS_API_HOST, headers, JSONUtil.toJsonStr(body));
        return JSONUtil.parseObj(responseBody);
    }

    private static byte[] hmac256(byte[] key, String msg) {
        return DigestUtil.hmac(HmacAlgorithm.HmacSHA256, key).digest(msg);
    }

}
