package com.focela.platform.system.config.sms.client.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.HmacAlgorithm;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.focela.platform.common.core.KeyValue;
import com.focela.platform.common.utils.http.HttpUtils;
import com.focela.platform.system.config.sms.client.dto.SmsReceiveRpcResponse;
import com.focela.platform.system.config.sms.client.dto.SmsSendRpcResponse;
import com.focela.platform.system.config.sms.client.dto.SmsTemplateRpcResponse;
import com.focela.platform.system.config.sms.enums.SmsTemplateAuditStatusEnum;
import com.focela.platform.system.config.sms.property.SmsChannelProperties;
import com.google.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.function.Function;

import static com.focela.platform.common.utils.collection.CollectionUtils.convertList;

/**
 * Qiniu Cloud SMS client implementation
 *
 * @since 2024/08/26 15:35
 */
@Slf4j
public class QiniuSmsClient extends AbstractSmsClient {

    private static final String HOST = "sms.qiniuapi.com";

    public QiniuSmsClient(SmsChannelProperties properties) {
        super(properties);
        Assert.notEmpty(properties.getApiKey(), "apiKey must not be blank");
        Assert.notEmpty(properties.getApiSecret(), "apiSecret must not be blank");
    }

    public SmsSendRpcResponse sendSms(Long sendLogId, String mobile, String apiTemplateId,
                                  List<KeyValue<String, Object>> templateParams) throws Throwable {
        // 1. execute the request
        // Reference: https://developer.qiniu.com/sms/5824/through-the-api-send-text-messages
        LinkedHashMap<String, Object> body = new LinkedHashMap<>();
        body.put("template_id", apiTemplateId);
        body.put("mobile", mobile);
        body.put("parameters", CollStreamUtil.toMap(templateParams, KeyValue::getKey, KeyValue::getValue));
        body.put("seq", Long.toString(sendLogId));
        JSONObject response = request("POST", body, "/v1/message/single");

        // 2. parse the response
        if (ObjectUtil.isNotEmpty(response.getStr("error"))) {
            // SMS request failed
            return new SmsSendRpcResponse().setSuccess(false)
                    .setApiCode(response.getStr("error"))
                    .setApiRequestId(response.getStr("request_id"))
                    .setApiMsg(response.getStr("message"));
        }
        return new SmsSendRpcResponse().setSuccess(response.containsKey("message_id"))
                .setSerialNo(response.getStr("message_id"));
    }

    /**
     * Send a request to Qiniu Cloud SMS
     *
     * @see <a href="https://developer.qiniu.com/sms/5842/sms-api-authentication">SMS API authentication</a>
     * @param httpMethod HTTP request method
     * @param body       HTTP request body
     * @param path       URL path
     * @return request result
     */
    private JSONObject request(String httpMethod, LinkedHashMap<String, Object> body, String path) {
        String signDate = DateUtil.date().setTimeZone(TimeZone.getTimeZone("UTC")).toString("yyyyMMdd'T'HHmmss'Z'");
        // 1. request headers
        Map<String, String> header = new HashMap<>(4);
        header.put("HOST", HOST);
        header.put("Authorization", getSignature(httpMethod, path, body != null ? JSONUtil.toJsonStr(body) : "", signDate));
        header.put("Content-Type", "application/json");
        header.put("X-Qiniu-Date", signDate);

        // 2. send the request
        String responseBody;
        if (Objects.equals(httpMethod, "POST")){
            responseBody = HttpUtils.post("https://" + HOST + path, header, JSONUtil.toJsonStr(body));
        } else {
            responseBody = HttpUtils.get("https://" + HOST + path, header);
        }
        return JSONUtil.parseObj(responseBody);
    }

    private String getSignature(String method, String path, String body, String signDate) {
        StringBuilder dataToSign = new StringBuilder();
        dataToSign.append(method.toUpperCase()).append(" ").append(path)
                .append("\nHost: ").append(HOST)
                .append("\n").append("Content-Type").append(": ").append("application/json")
                .append("\n").append("X-Qiniu-Date").append(": ").append(signDate)
                .append("\n\n");
        if (ObjectUtil.isNotEmpty(body)) {
            dataToSign.append(body);
        }
        String signature = SecureUtil.hmac(HmacAlgorithm.HmacSHA1, properties.getApiSecret())
                .digestBase64(dataToSign.toString(), true);
        return "Qiniu " + properties.getApiKey() + ":" + signature;
    }

    @Override
    public List<SmsReceiveRpcResponse> parseSmsReceiveStatus(String text) {
        JSONObject status = JSONUtil.parseObj(text);
        // Field reference: https://developer.qiniu.com/sms/5910/message-push
        return convertList(status.getJSONArray("items"), new Function<Object, SmsReceiveRpcResponse>() {

            @Override
            public SmsReceiveRpcResponse apply(Object item) {
                JSONObject statusObj = (JSONObject) item;
                return new SmsReceiveRpcResponse()
                        .setSuccess("DELIVRD".equals(statusObj.getStr("status"))) // whether received successfully
                        .setErrorMsg(statusObj.getStr("status")) // status report code
                        .setMobile(statusObj.getStr("mobile")) // mobile number
                        .setReceiveTime(LocalDateTimeUtil.of(statusObj.getLong("delivrd_at") * 1000L)) // status report time
                        .setSerialNo(statusObj.getStr("message_id")) // send serial number
                        .setLogId(statusObj.getLong("seq")); // user serial number
            }

        });
    }

    @Override
    public SmsTemplateRpcResponse getSmsTemplate(String apiTemplateId) throws Throwable {
        // 1. execute the request
        // Reference: https://developer.qiniu.com/sms/5969/query-a-single-template
        JSONObject response = request("GET", null, "/v1/template/" + apiTemplateId);

        // 2.2 parse the response
        return new SmsTemplateRpcResponse()
                .setId(response.getStr("id"))
                .setContent(response.getStr("template"))
                .setAuditStatus(convertSmsTemplateAuditStatus(response.getStr("audit_status")))
                .setAuditReason(response.getStr("reject_reason"));
    }

    @VisibleForTesting
    Integer convertSmsTemplateAuditStatus(String templateStatus) {
        switch (templateStatus) {
            case "passed": return SmsTemplateAuditStatusEnum.SUCCESS.getStatus();
            case "reviewing": return SmsTemplateAuditStatusEnum.CHECKING.getStatus();
            case "rejected": return SmsTemplateAuditStatusEnum.FAIL.getStatus();
            default:
                throw new IllegalArgumentException(String.format("unknown approval status (%str)", templateStatus));
        }
    }
}
