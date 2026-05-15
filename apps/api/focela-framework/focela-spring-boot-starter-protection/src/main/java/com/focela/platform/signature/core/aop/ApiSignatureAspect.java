package com.focela.platform.signature.core.aop;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.focela.platform.common.exception.ServiceException;
import com.focela.platform.common.exception.enums.GlobalErrorCodeConstants;
import com.focela.platform.common.utils.servlet.ServletUtils;
import com.focela.platform.signature.core.annotation.ApiSignature;
import com.focela.platform.signature.core.redis.ApiSignatureRedisDAO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.focela.platform.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;

/**
 * Intercept methods annotated with {@link ApiSignature} to enforce signature validation.
 */
@Aspect
@Slf4j
@AllArgsConstructor
public class ApiSignatureAspect {

    private final ApiSignatureRedisDAO signatureRedisDAO;

    @Before("@annotation(signature)")
    public void beforePointCut(JoinPoint joinPoint, ApiSignature signature) {
        // 1. Verification passed; exit early
        if (verifySignature(signature, Objects.requireNonNull(ServletUtils.getRequest()))) {
            return;
        }

        // 2. Verification failed; throw an exception
        log.error("[beforePointCut][method({}) args({}) signature failed]", joinPoint.getSignature().toString(),
                joinPoint.getArgs());
        throw new ServiceException(BAD_REQUEST.getCode(),
                StrUtil.blankToDefault(signature.message(), BAD_REQUEST.getMsg()));
    }

    public boolean verifySignature(ApiSignature signature, HttpServletRequest request) {
        // 1.1 Validate headers
        if (!verifyHeaders(signature, request)) {
            return false;
        }
        // 1.2 Validate that the appId resolves to a known appSecret
        String appId = request.getHeader(signature.appId());
        String appSecret = signatureRedisDAO.getAppSecret(appId);
        Assert.notNull(appSecret, "[appId({})] no matching appSecret found", appId);

        // 2. Verify the signature [important!]
        String clientSignature = request.getHeader(signature.sign()); // client-side signature
        String serverSignatureString = buildSignatureString(signature, request, appSecret); // server-side signature input string
        String serverSignature = DigestUtil.sha256Hex(serverSignatureString); // server-side signature
        if (ObjUtil.notEqual(clientSignature, serverSignature)) {
            return false;
        }

        // 3. Cache the nonce to prevent reuse (note: TTL must be 2x the allowed timestamp skew)
        String nonce = request.getHeader(signature.nonce());
        if (BooleanUtil.isFalse(signatureRedisDAO.setNonce(appId, nonce, signature.timeout() * 2, signature.timeUnit()))) {
            String timestamp = request.getHeader(signature.timestamp());
            log.info("[verifySignature][appId({}) timestamp({}) nonce({}) sign({}) duplicate request]", appId, timestamp, nonce, clientSignature);
            throw new ServiceException(GlobalErrorCodeConstants.REPEATED_REQUESTS.getCode(), "Duplicate request detected");
        }
        return true;
    }

    /**
     * Validate the signature headers.
     * <p>
     * 1. appId is not blank.
     * 2. timestamp is not blank and the request is not expired (default 10 minutes).
     * 3. nonce is not blank, at least 10 characters, and has not been used within the allowed window.
     * 4. sign is not blank.
     *
     * @param signature signature
     * @param request   request
     * @return whether the headers passed validation
     */
    private boolean verifyHeaders(ApiSignature signature, HttpServletRequest request) {
        // 1. Required-field checks
        String appId = request.getHeader(signature.appId());
        if (StrUtil.isBlank(appId)) {
            return false;
        }
        String timestamp = request.getHeader(signature.timestamp());
        if (StrUtil.isBlank(timestamp)) {
            return false;
        }
        String nonce = request.getHeader(signature.nonce());
        if (StrUtil.length(nonce) < 10) {
            return false;
        }
        String sign = request.getHeader(signature.sign());
        if (StrUtil.isBlank(sign)) {
            return false;
        }

        // 2. Verify the timestamp is within the allowed window (note: use absolute value)
        long expireTime = signature.timeUnit().toMillis(signature.timeout());
        long requestTimestamp = Long.parseLong(timestamp);
        long timestampDisparity = Math.abs(System.currentTimeMillis() - requestTimestamp);
        if (timestampDisparity > expireTime) {
            return false;
        }

        // 3. Verify the nonce has not been used; each value may be used only once
        return signatureRedisDAO.getNonce(appId, nonce) == null;
    }

    /**
     * Build the signature input string.
     * <p>
     * Format: request parameters + request body + request headers + secret.
     *
     * @param signature signature
     * @param request   request
     * @param appSecret appSecret
     * @return signature input string
     */
    private String buildSignatureString(ApiSignature signature, HttpServletRequest request, String appSecret) {
        SortedMap<String, String> parameterMap = getRequestParameterMap(request); // request parameters
        SortedMap<String, String> headerMap = getRequestHeaderMap(signature, request); // request headers
        String requestBody = StrUtil.nullToDefault(ServletUtils.getBody(request), ""); // request body
        return MapUtil.join(parameterMap, "&", "=")
                + requestBody
                + MapUtil.join(headerMap, "&", "=")
                + appSecret;
    }

    /**
     * Build the map of signature header parameters.
     *
     * @param request   request
     * @param signature signature annotation
     * @return signature params
     */
    private static SortedMap<String, String> getRequestHeaderMap(ApiSignature signature, HttpServletRequest request) {
        SortedMap<String, String> sortedMap = new TreeMap<>();
        sortedMap.put(signature.appId(), request.getHeader(signature.appId()));
        sortedMap.put(signature.timestamp(), request.getHeader(signature.timestamp()));
        sortedMap.put(signature.nonce(), request.getHeader(signature.nonce()));
        return sortedMap;
    }

    /**
     * Build the map of request parameters.
     *
     * @param request request
     * @return queryParams
     */
    private static SortedMap<String, String> getRequestParameterMap(HttpServletRequest request) {
        SortedMap<String, String> sortedMap = new TreeMap<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            sortedMap.put(entry.getKey(), entry.getValue()[0]);
        }
        return sortedMap;
    }

}