package com.focela.platform.web.core.handler;

import com.focela.platform.common.model.CommonResult;
import com.focela.platform.web.core.utils.WebFrameworkUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Global response body handler
 *
 * Unlike many articles on the web that choose to automatically wrap the Controller return value with {@link CommonResult},
 * here the Controller actively wraps its return value with {@link CommonResult} when returning.
 * The reason is that GlobalResponseBodyHandler is essentially AOP; it should not change the data structure returned by the Controller.
 *
 * Currently, the main purpose of GlobalResponseBodyHandler is to record the Controller return value,
 * making it easier for {@link com.focela.platform.apilog.core.filter.ApiAccessLogFilter} to record access logs.
 */
@ControllerAdvice
public class GlobalResponseBodyHandler implements ResponseBodyAdvice {

    @Override
    @SuppressWarnings("NullableProblems") // avoid IDEA warning
    public boolean supports(MethodParameter returnType, Class converterType) {
        if (returnType.getMethod() == null) {
            return false;
        }
        // only intercept results of type CommonResult
        return returnType.getMethod().getReturnType() == CommonResult.class;
    }

    @Override
    @SuppressWarnings("NullableProblems") // avoid IDEA warning
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        // record Controller result
        WebFrameworkUtils.setCommonResult(((ServletServerHttpRequest) request).getServletRequest(), (CommonResult<?>) body);
        return body;
    }

}
