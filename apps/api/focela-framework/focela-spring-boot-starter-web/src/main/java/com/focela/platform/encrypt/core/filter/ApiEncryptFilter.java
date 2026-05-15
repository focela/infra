package com.focela.platform.encrypt.core.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.AsymmetricDecryptor;
import cn.hutool.crypto.asymmetric.AsymmetricEncryptor;
import cn.hutool.crypto.symmetric.SymmetricDecryptor;
import cn.hutool.crypto.symmetric.SymmetricEncryptor;
import com.focela.platform.common.model.CommonResult;
import com.focela.platform.common.utils.object.ObjectUtils;
import com.focela.platform.common.utils.servlet.ServletUtils;
import com.focela.platform.encrypt.config.ApiEncryptProperties;
import com.focela.platform.encrypt.core.annotation.ApiEncrypt;
import com.focela.platform.web.config.WebProperties;
import com.focela.platform.web.core.filter.ApiRequestFilter;
import com.focela.platform.web.core.handler.GlobalExceptionHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.ServletRequestPathUtils;

import java.io.IOException;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.invalidParamException;

/**
 * API encryption filter that processes the {@link ApiEncrypt} annotation.
 *
 * 1. Decrypts request parameters
 * 2. Encrypts response results
 *
 * Q: Why not use SpringMVC's RequestBodyAdvice or ResponseBodyAdvice mechanism?
 * A: Considering that the project records access logs, error logs, and HTTP API signing,
 * it is best to handle this globally and parse early.
 */
@Slf4j
public class ApiEncryptFilter extends ApiRequestFilter {

    private final ApiEncryptProperties apiEncryptProperties;

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    private final GlobalExceptionHandler globalExceptionHandler;

    private final SymmetricDecryptor requestSymmetricDecryptor;
    private final AsymmetricDecryptor requestAsymmetricDecryptor;

    private final SymmetricEncryptor responseSymmetricEncryptor;
    private final AsymmetricEncryptor responseAsymmetricEncryptor;

    public ApiEncryptFilter(WebProperties webProperties,
                            ApiEncryptProperties apiEncryptProperties,
                            RequestMappingHandlerMapping requestMappingHandlerMapping,
                            GlobalExceptionHandler globalExceptionHandler) {
        super(webProperties);
        this.apiEncryptProperties = apiEncryptProperties;
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
        this.globalExceptionHandler = globalExceptionHandler;
        if (StrUtil.equalsIgnoreCase(apiEncryptProperties.getAlgorithm(), "AES")) {
            this.requestSymmetricDecryptor = SecureUtil.aes(StrUtil.utf8Bytes(apiEncryptProperties.getRequestKey()));
            this.requestAsymmetricDecryptor = null;
            this.responseSymmetricEncryptor = SecureUtil.aes(StrUtil.utf8Bytes(apiEncryptProperties.getResponseKey()));
            this.responseAsymmetricEncryptor = null;
        } else if (StrUtil.equalsIgnoreCase(apiEncryptProperties.getAlgorithm(), "RSA")) {
            this.requestSymmetricDecryptor = null;
            this.requestAsymmetricDecryptor = SecureUtil.rsa(apiEncryptProperties.getRequestKey(), null);
            this.responseSymmetricEncryptor = null;
            this.responseAsymmetricEncryptor = SecureUtil.rsa(null, apiEncryptProperties.getResponseKey());
        } else {
            // Note: to support SM2, SM4, etc., add the corresponding instance creation here and the matching Maven dependency.
            throw new IllegalArgumentException("not supported encryption algorithm:" + apiEncryptProperties.getAlgorithm());
        }
    }

    @Override
    @SuppressWarnings("NullableProblems")
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // get the @ApiEncrypt annotation
        ApiEncrypt apiEncrypt = getApiEncrypt(request);
        boolean requestEnable = apiEncrypt != null && apiEncrypt.request();
        boolean responseEnable = apiEncrypt != null && apiEncrypt.response();
        String encryptHeader = request.getHeader(apiEncryptProperties.getHeader());
        if (!requestEnable && !responseEnable && StrUtil.isBlank(encryptHeader))  {
            chain.doFilter(request, response);
            return;
        }

        // 1. decrypt request
        if (ObjectUtils.equalsAny(HttpMethod.valueOf(request.getMethod()),
                HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)) {
            try {
                if (StrUtil.isNotBlank(encryptHeader)) {
                    request = new ApiDecryptRequestWrapper(request,
                            requestSymmetricDecryptor, requestAsymmetricDecryptor);
                } else if (requestEnable) {
                    throw invalidParamException("request does not contain the encrypt header; please verify it is configured correctly");
                }
            } catch (Exception ex) {
                CommonResult<?> result = globalExceptionHandler.allExceptionHandler(request, ex);
                ServletUtils.writeJSON(response, result);
                return;
            }
        }

        // 2. execute the filter chain
        if (responseEnable) {
            // Special: only wrap here, execute later. Purpose: allow the response content to be read multiple times.
            response = new ApiEncryptResponseWrapper(response);
        }
        chain.doFilter(request, response);

        // 3. encrypt the response (actual execution)
        if (responseEnable) {
            ((ApiEncryptResponseWrapper) response).encrypt(apiEncryptProperties,
                    responseSymmetricEncryptor, responseAsymmetricEncryptor);
        }
    }

    /**
     * Get the @ApiEncrypt annotation.
     *
     * @param request request
     */
    @SuppressWarnings("PatternVariableCanBeUsed")
    private ApiEncrypt getApiEncrypt(HttpServletRequest request) {
        try {
            // Special: compatibility fix for an error in SpringBoot 2.X https://t.zsxq.com/kqyiB
            if (!ServletRequestPathUtils.hasParsedRequestPath(request)) {
                ServletRequestPathUtils.parseAndCache(request);
            }

            // resolve the @ApiEncrypt annotation
            HandlerExecutionChain mappingHandler = requestMappingHandlerMapping.getHandler(request);
            if (mappingHandler == null) {
                return null;
            }
            Object handler = mappingHandler.getHandler();
            if (handler instanceof HandlerMethod) {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                ApiEncrypt annotation = handlerMethod.getMethodAnnotation(ApiEncrypt.class);
                if (annotation == null) {
                    annotation = handlerMethod.getBeanType().getAnnotation(ApiEncrypt.class);
                }
                return annotation;
            }
        } catch (Exception e) {
            log.error("[getApiEncrypt][url({}/{}) failed to get @ApiEncrypt annotation]",
                    request.getRequestURI(), request.getMethod(), e);
        }
        return null;
    }

}
