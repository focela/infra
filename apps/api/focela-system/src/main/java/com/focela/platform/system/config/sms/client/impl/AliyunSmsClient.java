package com.focela.platform.system.config.sms.client.impl;

import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.focela.platform.common.core.KeyValue;
import com.focela.platform.common.utils.collection.MapUtils;
import com.focela.platform.common.utils.http.HttpUtils;
import com.focela.platform.common.utils.json.JsonUtils;
import com.focela.platform.system.config.sms.client.dto.SmsReceiveRpcResponse;
import com.focela.platform.system.config.sms.client.dto.SmsSendRpcResponse;
import com.focela.platform.system.config.sms.client.dto.SmsTemplateRpcResponse;
import com.focela.platform.system.config.sms.enums.SmsTemplateAuditStatusEnum;
import com.focela.platform.system.config.sms.property.SmsChannelProperties;
import com.google.common.annotations.VisibleForTesting;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

import static com.focela.platform.common.utils.collection.CollectionUtils.convertList;

/**
 * Aliyun SMS client implementation
 *
 * @since 2021/1/25 14:17
 */
@Slf4j
public class AliyunSmsClient extends AbstractSmsClient {

    private static final String URL = "https://dysmsapi.aliyuncs.com";
    private static final String HOST = "dysmsapi.aliyuncs.com";
    private static final String VERSION = "2017-05-25";

    private static final String RESPONSE_CODE_SUCCESS = "OK";

    public AliyunSmsClient(SmsChannelProperties properties) {
        super(properties);
        Assert.notEmpty(properties.getApiKey(), "apiKey must not be blank");
        Assert.notEmpty(properties.getApiSecret(), "apiSecret must not be blank");
    }

    @Override
    public SmsSendRpcResponse sendSms(Long sendLogId, String mobile, String apiTemplateId,
                                  List<KeyValue<String, Object>> templateParams) throws Throwable {
        Assert.notBlank(properties.getSignature(), "SMS signature must not be blank");
        // 1. execute the request
        // Reference: https://api.aliyun.com/document/Dysmsapi/2017-05-25/SendSms
        TreeMap<String, Object> queryParam = new TreeMap<>();
        queryParam.put("PhoneNumbers", mobile);
        queryParam.put("SignName", properties.getSignature());
        queryParam.put("TemplateCode", apiTemplateId);
        queryParam.put("TemplateParam", JsonUtils.toJsonString(MapUtils.convertMap(templateParams)));
        queryParam.put("OutId", sendLogId);
        JSONObject response = request("SendSms", queryParam);

        // 2. parse the response
        return new SmsSendRpcResponse()
                .setSuccess(Objects.equals(response.getStr("Code"), RESPONSE_CODE_SUCCESS))
                .setSerialNo(response.getStr("BizId"))
                .setApiRequestId(response.getStr("RequestId"))
                .setApiCode(response.getStr("Code"))
                .setApiMsg(response.getStr("Message"));
    }

    @Override
    public List<SmsReceiveRpcResponse> parseSmsReceiveStatus(String text) {
        JSONArray statuses = JSONUtil.parseArray(text);
        // Field reference: https://help.aliyun.com/zh/sms/developer-reference/smsreport-2
        return convertList(statuses, status -> {
            JSONObject statusObj = (JSONObject) status;
            return new SmsReceiveRpcResponse()
                    .setSuccess(statusObj.getBool("success")) // whether received successfully
                    .setErrorCode(statusObj.getStr("err_code")) // status report code
                    .setErrorMsg(statusObj.getStr("err_msg")) // status report description
                    .setMobile(statusObj.getStr("phone_number")) // mobile number
                    .setReceiveTime(statusObj.getLocalDateTime("report_time", null)) // status report time
                    .setSerialNo(statusObj.getStr("biz_id")) // send serial number
                    .setLogId(statusObj.getLong("out_id")); // user serial number
        });
    }

    @Override
    public SmsTemplateRpcResponse getSmsTemplate(String apiTemplateId) throws Throwable {
        // 1. execute the request
        // Reference: https://api.aliyun.com/document/Dysmsapi/2017-05-25/GetSmsTemplate
        TreeMap<String, Object> queryParam = new TreeMap<>();
        queryParam.put("TemplateCode", apiTemplateId);
        JSONObject response = request("GetSmsTemplate", queryParam);

        // 2.1 request failed
        String code = response.getStr("Code");
        if (ObjectUtil.notEqual(code, RESPONSE_CODE_SUCCESS)) {
            log.error("[getSmsTemplate][template ID ({}) invalid response ({})]", apiTemplateId, response);
            return null;
        }
        // 2.2 request succeeded
        return new SmsTemplateRpcResponse()
                .setId(response.getStr("TemplateCode"))
                .setContent(response.getStr("TemplateContent"))
                .setAuditStatus(convertSmsTemplateAuditStatus(response.getInt("TemplateStatus")))
                .setAuditReason(response.getStr("Reason"));
    }

    @VisibleForTesting
    @SuppressWarnings("EnhancedSwitchMigration")
    Integer convertSmsTemplateAuditStatus(Integer templateStatus) {
        switch (templateStatus) {
            case 0: return SmsTemplateAuditStatusEnum.CHECKING.getStatus();
            case 1: return SmsTemplateAuditStatusEnum.SUCCESS.getStatus();
            case 2: return SmsTemplateAuditStatusEnum.FAIL.getStatus();
            default: throw new IllegalArgumentException(String.format("unknown approval status (%d)", templateStatus));
        }
    }

    /**
     * Send a request to Aliyun SMS
     *
     * @see <a href="https://help.aliyun.com/zh/sdk/product-overview/v3-request-structure-and-signature">V3 request body and signature mechanism</a>
     * @param apiName     name of the API to request
     * @param queryParams request parameters
     * @return request result
     */
    private JSONObject request(String apiName, TreeMap<String, Object> queryParams) {
        // 1. request parameters
        String queryString = queryParams.entrySet().stream()
                .map(entry -> percentCode(entry.getKey()) + "=" + percentCode(String.valueOf(entry.getValue())))
                .collect(Collectors.joining("&"));

        // 2. request body
        String requestBody = ""; // The SMS API is an RPC interface; query parameters are appended in the URI, so the request body is left empty unless special handling is required.
        String hashedRequestPayload = DigestUtil.sha256Hex(requestBody);

        // 3.1 request headers
        TreeMap<String, String> headers = new TreeMap<>();
        headers.put("host", HOST);
        headers.put("x-acs-version", VERSION);
        headers.put("x-acs-action", apiName);
        headers.put("x-acs-date", FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss'Z'", TimeZone.getTimeZone("GMT")).format(new Date()));
        headers.put("x-acs-signature-nonce", IdUtil.randomUUID());
        headers.put("x-acs-content-sha256", hashedRequestPayload);

        // 3.2 build the signed headers
        StringBuilder canonicalHeaders = new StringBuilder(); // build the canonical headers; multiple canonical message headers are concatenated in ascending order by the (lowercase) header name's character code
        StringBuilder signedHeadersBuilder = new StringBuilder(); // list of signed headers; multiple header names (lowercase) are sorted in ascending order by first letter and separated by semicolons (;)
        headers.entrySet().stream().filter(entry -> entry.getKey().toLowerCase().startsWith("x-acs-")
                        || "host".equalsIgnoreCase(entry.getKey())
                        || "content-type".equalsIgnoreCase(entry.getKey()))
                .sorted(Map.Entry.comparingByKey()).forEach(entry -> {
                    String lowerKey = entry.getKey().toLowerCase();
                    canonicalHeaders.append(lowerKey).append(":").append(String.valueOf(entry.getValue()).trim()).append("\n");
                    signedHeadersBuilder.append(lowerKey).append(";");
                });
        String signedHeaders = signedHeadersBuilder.substring(0, signedHeadersBuilder.length() - 1);

        // 4. build the Authorization signature
        String canonicalRequest = "POST" + "\n" +
                "/" + "\n" +
                queryString + "\n" +
                canonicalHeaders + "\n" +
                signedHeaders + "\n" +
                hashedRequestPayload;
        String hashedCanonicalRequest = DigestUtil.sha256Hex(canonicalRequest);
        String stringToSign = "ACS3-HMAC-SHA256" + "\n" + hashedCanonicalRequest;
        String signature = SecureUtil.hmacSha256(properties.getApiSecret()).digestHex(stringToSign); // compute the signature
        headers.put("Authorization", "ACS3-HMAC-SHA256" + " " + "Credential=" + properties.getApiKey()
                + ", " + "SignedHeaders=" + signedHeaders + ", " + "Signature=" + signature);

        // 5. send the request
        String responseBody = HttpUtils.post(URL + "?" + queryString, headers, requestBody);
        return JSONUtil.parseObj(responseBody);
    }

    /**
     * URL-encode the given string and replace specific characters to conform to URL encoding spec
     *
     * @param str string to URL-encode
     * @return encoded string
     */
    @SneakyThrows
    private static String percentCode(String str) {
        Assert.notNull(str, "str must not be null");
        return HttpUtils.encodeUtf8(str)
                .replace("+", "%20") // plus sign "+" is replaced by "%20"
                .replace("*", "%2A") // asterisk "*" is replaced by "%2A"
                .replace("%7E", "~"); // tilde "%7E" is replaced by "~"
    }

}
