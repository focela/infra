package com.focela.platform.tracer.core.aop;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.tracer.core.annotation.BusinessTrace;
import com.focela.platform.common.utils.spring.SpringExpressionUtils;
import com.focela.platform.tracer.core.utils.TracerFrameworkUtils;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.Map;

import static java.util.Arrays.asList;

/**
 * {@link BusinessTrace} aspect, records the business trace.
 */
@Aspect
@AllArgsConstructor
@Slf4j
public class BusinessTraceAspect {

    private static final String BIZ_OPERATION_NAME_PREFIX = "Biz/";

    private final Tracer tracer;

    @Around(value = "@annotation(trace)")
    public Object around(ProceedingJoinPoint joinPoint, BusinessTrace trace) throws Throwable {
        // Create span
        String operationName = getOperationName(joinPoint, trace);
        Span span = tracer.buildSpan(operationName)
                .withTag(Tags.COMPONENT.getKey(), "biz")
                .start();
        try {
            // Invoke the original method
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            TracerFrameworkUtils.onError(throwable, span);
            throw throwable;
        } finally {
            // Set the biz tags on the Span
            setBizTag(span, joinPoint, trace);
            // Finish the Span
            span.finish();
        }
    }

    private String getOperationName(ProceedingJoinPoint joinPoint, BusinessTrace trace) {
        // Custom operation name
        if (StrUtil.isNotEmpty(trace.operationName())) {
            return BIZ_OPERATION_NAME_PREFIX + trace.operationName();
        }
        // Default operation name uses the method name
        return BIZ_OPERATION_NAME_PREFIX
                + joinPoint.getSignature().getDeclaringType().getSimpleName()
                + "/" + joinPoint.getSignature().getName();
    }

    private void setBizTag(Span span, ProceedingJoinPoint joinPoint, BusinessTrace trace) {
        try {
            Map<String, Object> result = SpringExpressionUtils.parseExpressions(joinPoint, asList(trace.type(), trace.id()));
            span.setTag(BusinessTrace.TYPE_TAG, MapUtil.getStr(result, trace.type()));
            span.setTag(BusinessTrace.ID_TAG, MapUtil.getStr(result, trace.id()));
        } catch (Exception ex) {
            log.error("[setBizTag][exception occurred while parsing bizType and bizId]", ex);
        }
    }

}
