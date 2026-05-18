package com.focela.platform.signature.core;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.focela.platform.signature.core.annotation.ApiSignature;
import com.focela.platform.signature.core.aop.ApiSignatureAspect;
import com.focela.platform.signature.core.redis.ApiSignatureRedisRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ApiSignatureTest}.
 */
@ExtendWith(MockitoExtension.class)
public class ApiSignatureTest {

    @InjectMocks
    private ApiSignatureAspect apiSignatureAspect;

    @Mock
    private ApiSignatureRedisRepository signatureRedisRepository;

    @Test
    public void testSignatureGet() throws IOException {
        // Build a signature
        Long timestamp = System.currentTimeMillis();
        String nonce = IdUtil.randomUUID();
        String appId = "xxxxxx";
        String appSecret = "yyyyyy";
        String signString = "k1=v1&v1=k1testappId=xxxxxx&nonce=" + nonce + "&timestamp=" + timestamp + "yyyyyy";
        String sign = DigestUtil.sha256Hex(signString);

        // Prepare parameters
        ApiSignature apiSignature = mock(ApiSignature.class);
        when(apiSignature.appId()).thenReturn("appId");
        when(apiSignature.timestamp()).thenReturn("timestamp");
        when(apiSignature.nonce()).thenReturn("nonce");
        when(apiSignature.sign()).thenReturn("sign");
        when(apiSignature.timeout()).thenReturn(60);
        when(apiSignature.timeUnit()).thenReturn(TimeUnit.SECONDS);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(eq("appId"))).thenReturn(appId);
        when(request.getHeader(eq("timestamp"))).thenReturn(String.valueOf(timestamp));
        when(request.getHeader(eq("nonce"))).thenReturn(nonce);
        when(request.getHeader(eq("sign"))).thenReturn(sign);
        when(request.getParameterMap()).thenReturn(MapUtil.<String, String[]>builder()
                .put("v1", new String[]{"k1"}).put("k1", new String[]{"v1"}).build());
        when(request.getContentType()).thenReturn("application/json");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("test")));
        // mock the methods
        when(signatureRedisRepository.getAppSecret(eq(appId))).thenReturn(appSecret);
        when(signatureRedisRepository.setNonce(eq(appId), eq(nonce), eq(120), eq(TimeUnit.SECONDS))).thenReturn(true);

        // invoke
        boolean result = apiSignatureAspect.verifySignature(apiSignature, request);
        // assert the result
        assertTrue(result);
    }

}