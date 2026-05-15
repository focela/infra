package com.focela.platform.signature.core.redis;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * HTTP API signature Redis DAO.
 */
@AllArgsConstructor
public class ApiSignatureRedisDAO {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * Signature nonce.
     * <p>
     * KEY format: signature_nonce:%s // parameter is the random nonce
     * VALUE format: String
     * Expiration: variable
     */
    private static final String SIGNATURE_NONCE = "api_signature_nonce:%s:%s";

    /**
     * Signature secret.
     * <p>
     * HASH structure.
     * KEY format: %s // parameter is the appId
     * VALUE format: String
     * Expiration: never expires (preloaded into Redis)
     */
    private static final String SIGNATURE_APPID = "api_signature_app";

    // ========== Signature nonce ==========

    public String getNonce(String appId, String nonce) {
        return stringRedisTemplate.opsForValue().get(formatNonceKey(appId, nonce));
    }

    public Boolean setNonce(String appId, String nonce, int time, TimeUnit timeUnit) {
        return stringRedisTemplate.opsForValue().setIfAbsent(formatNonceKey(appId, nonce), "", time, timeUnit);
    }

    private static String formatNonceKey(String appId, String nonce) {
        return String.format(SIGNATURE_NONCE, appId, nonce);
    }

    // ========== Signature secret ==========

    public String getAppSecret(String appId) {
        return (String) stringRedisTemplate.opsForHash().get(SIGNATURE_APPID, appId);
    }

}
