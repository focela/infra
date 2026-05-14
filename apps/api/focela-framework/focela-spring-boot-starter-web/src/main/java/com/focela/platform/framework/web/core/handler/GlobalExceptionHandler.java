package com.focela.platform.framework.web.core.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.framework.common.contract.infra.logger.ApiErrorLogContractApi;
import com.focela.platform.framework.common.contract.infra.logger.dto.ApiErrorLogCreateRpcRequest;
import com.focela.platform.framework.common.exception.ServiceException;
import com.focela.platform.framework.common.exception.utils.ServiceExceptionUtils;
import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.framework.common.utils.collection.SetUtils;
import com.focela.platform.framework.common.utils.json.JsonUtils;
import com.focela.platform.framework.common.utils.monitor.TracerUtils;
import com.focela.platform.framework.common.utils.servlet.ServletUtils;
import com.focela.platform.framework.web.core.utils.WebFrameworkUtils;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.google.common.util.concurrent.UncheckedExecutionException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.focela.platform.framework.common.exception.enums.GlobalErrorCodeConstants.*;

/**
 * Global exception handler; translates exceptions into CommonResult + corresponding error code
 */
@RestControllerAdvice
@AllArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    /**
     * ServiceException error messages to ignore, to avoid logging too much
     */
    // NOTE: value compared against error messages thrown elsewhere; kept in Chinese intentionally ("Invalid refresh token")
    public static final Set<String> IGNORE_ERROR_MESSAGES = SetUtils.asSet("无效的刷新令牌");

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private final String applicationName;

    private final ApiErrorLogContractApi apiErrorLogApi;

    /**
     * Handle all exceptions, mainly for Filter usage.
     * Because Filter does not go through the SpringMVC flow, but we still need a fallback exception handler,
     * a full exception handling process is provided here to keep logic consistent.
     *
     * @param request request
     * @param ex exception
     * @return common result
     */
    public CommonResult<?> allExceptionHandler(HttpServletRequest request, Throwable ex) {
        if (ex instanceof MissingServletRequestParameterException) {
            return missingServletRequestParameterExceptionHandler((MissingServletRequestParameterException) ex);
        }
        if (ex instanceof MethodArgumentTypeMismatchException) {
            return methodArgumentTypeMismatchExceptionHandler((MethodArgumentTypeMismatchException) ex);
        }
        if (ex instanceof MethodArgumentNotValidException) {
            return methodArgumentNotValidExceptionExceptionHandler((MethodArgumentNotValidException) ex);
        }
        if (ex instanceof BindException) {
            return bindExceptionHandler((BindException) ex);
        }
        if (ex instanceof ConstraintViolationException) {
            return constraintViolationExceptionHandler((ConstraintViolationException) ex);
        }
        if (ex instanceof ValidationException) {
            return validationException((ValidationException) ex);
        }
        if (ex instanceof MaxUploadSizeExceededException) {
            return maxUploadSizeExceededExceptionHandler((MaxUploadSizeExceededException) ex);
        }
        if (ex instanceof NoHandlerFoundException) {
            return noHandlerFoundExceptionHandler((NoHandlerFoundException) ex);
        }
        if (ex instanceof NoResourceFoundException) {
            return noResourceFoundExceptionHandler(request, (NoResourceFoundException) ex);
        }
        if (ex instanceof HttpRequestMethodNotSupportedException) {
            return httpRequestMethodNotSupportedExceptionHandler((HttpRequestMethodNotSupportedException) ex);
        }
        if (ex instanceof HttpMediaTypeNotSupportedException) {
            return httpMediaTypeNotSupportedExceptionHandler((HttpMediaTypeNotSupportedException) ex);
        }
        if (ex instanceof ServiceException) {
            return serviceExceptionHandler((ServiceException) ex);
        }
        if (ex instanceof AccessDeniedException) {
            return accessDeniedExceptionHandler(request, (AccessDeniedException) ex);
        }
        return defaultExceptionHandler(request, ex);
    }

    /**
     * Handle SpringMVC missing request parameter
     *
     * For example, the interface declares @RequestParam("xx"), but xx is not provided.
     */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public CommonResult<?> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException ex) {
        log.warn("[missingServletRequestParameterExceptionHandler]", ex);
        return CommonResult.error(BAD_REQUEST.getCode(), String.format("request param missing:%s", ex.getParameterName()));
    }

    /**
     * Handle SpringMVC request parameter type mismatch
     *
     * For example, the interface declares @RequestParam("xx") as Integer, but xx is provided as a String.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public CommonResult<?> methodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException ex) {
        log.warn("[methodArgumentTypeMismatchExceptionHandler]", ex);
        return CommonResult.error(BAD_REQUEST.getCode(), String.format("invalid request param type:%s", ex.getMessage()));
    }

    /**
     * Handle SpringMVC parameter validation failures
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult<?> methodArgumentNotValidExceptionExceptionHandler(MethodArgumentNotValidException ex) {
        log.warn("[methodArgumentNotValidExceptionExceptionHandler]", ex);
        // get errorMessage
        String errorMessage = null;
        FieldError fieldError = ex.getBindingResult().getFieldError();
        if (fieldError == null) {
            // combined validation, reference: https://t.zsxq.com/3HVTx
            List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();
            if (CollUtil.isNotEmpty(allErrors)) {
                errorMessage = allErrors.get(0).getDefaultMessage();
            }
        } else {
            errorMessage = fieldError.getDefaultMessage();
        }
        // convert to CommonResult
        if (StrUtil.isEmpty(errorMessage)) {
            return CommonResult.error(BAD_REQUEST);
        }
        return CommonResult.error(BAD_REQUEST.getCode(), String.format("invalid request param:%s", errorMessage));
    }

    /**
     * Handle SpringMVC parameter binding failures; essentially also Validator validation
     */
    @ExceptionHandler(BindException.class)
    public CommonResult<?> bindExceptionHandler(BindException ex) {
        log.warn("[handleBindException]", ex);
        FieldError fieldError = ex.getFieldError();
        assert fieldError != null; // assertion, avoids warning
        return CommonResult.error(BAD_REQUEST.getCode(), String.format("invalid request param:%s", fieldError.getDefaultMessage()));
    }

    /**
     * Handle SpringMVC request parameter type errors
     *
     * For example, the @RequestBody entity declares an Integer property xx, but xx is provided as a String.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @SuppressWarnings("PatternVariableCanBeUsed")
    public CommonResult<?> methodArgumentTypeInvalidFormatExceptionHandler(HttpMessageNotReadableException ex) {
        log.warn("[methodArgumentTypeInvalidFormatExceptionHandler]", ex);
        if (ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException invalidFormatException = (InvalidFormatException) ex.getCause();
            return CommonResult.error(BAD_REQUEST.getCode(), String.format("invalid request param type:%s", invalidFormatException.getValue()));
        }
        if (StrUtil.startWith(ex.getMessage(), "Required request body is missing")) {
            return CommonResult.error(BAD_REQUEST.getCode(), "invalid request param type: request body missing");
        }
        return defaultExceptionHandler(ServletUtils.getRequest(), ex);
    }

    /**
     * Handle exceptions from Validator validation failures
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public CommonResult<?> constraintViolationExceptionHandler(ConstraintViolationException ex) {
        log.warn("[constraintViolationExceptionHandler]", ex);
        ConstraintViolation<?> constraintViolation = ex.getConstraintViolations().iterator().next();
        return CommonResult.error(BAD_REQUEST.getCode(), String.format("invalid request param:%s", constraintViolation.getMessage()));
    }

    /**
     * Handle ValidationException thrown by Dubbo Consumer local parameter validation
     */
    @ExceptionHandler(value = ValidationException.class)
    public CommonResult<?> validationException(ValidationException ex) {
        log.warn("[constraintViolationExceptionHandler]", ex);
        // Cannot construct detailed error info because Dubbo Consumer throws ValidationException with a raw, non-human-readable string
        return CommonResult.error(BAD_REQUEST);
    }

    /**
     * Handle exception when uploaded file is too large
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public CommonResult<?> maxUploadSizeExceededExceptionHandler(MaxUploadSizeExceededException ex) {
        return CommonResult.error(BAD_REQUEST.getCode(), "Uploaded file is too large, please adjust and retry");
    }

    /**
     * Handle SpringMVC request URL not found
     *
     * Note: it requires the following two configuration items:
     * 1. spring.mvc.throw-exception-if-no-handler-found = true
     * 2. spring.mvc.static-path-pattern = /statics/**
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public CommonResult<?> noHandlerFoundExceptionHandler(NoHandlerFoundException ex) {
        log.warn("[noHandlerFoundExceptionHandler]", ex);
        return CommonResult.error(NOT_FOUND.getCode(), String.format("request URL does not exist:%s", ex.getRequestURL()));
    }

    /**
     * Handle SpringMVC request URL not found
     */
    @ExceptionHandler(NoResourceFoundException.class)
    private CommonResult<?> noResourceFoundExceptionHandler(HttpServletRequest req, NoResourceFoundException ex) {
        log.warn("[noResourceFoundExceptionHandler]", ex);
        return CommonResult.error(NOT_FOUND.getCode(), String.format("request URL does not exist:%s", ex.getResourcePath()));
    }

    /**
     * Handle SpringMVC request method not supported
     *
     * For example, interface A is defined as GET but the request is POST, causing a mismatch.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public CommonResult<?> httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException ex) {
        log.warn("[httpRequestMethodNotSupportedExceptionHandler]", ex);
        return CommonResult.error(METHOD_NOT_ALLOWED.getCode(), String.format("HTTP method is invalid:%s", ex.getMessage()));
    }

    /**
     * Handle SpringMVC unsupported Content-Type
     *
     * For example, interface A's Content-Type is application/json but the request's Content-Type is application/octet-stream, causing a mismatch.
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public CommonResult<?> httpMediaTypeNotSupportedExceptionHandler(HttpMediaTypeNotSupportedException ex) {
        log.warn("[httpMediaTypeNotSupportedExceptionHandler]", ex);
        return CommonResult.error(BAD_REQUEST.getCode(), String.format("request type is invalid:%s", ex.getMessage()));
    }

    /**
     * Handle Spring Security access-denied exception
     *
     * Originates from using @PreAuthorize annotation with AOP for permission checking.
     */
    @ExceptionHandler(value = AccessDeniedException.class)
    public CommonResult<?> accessDeniedExceptionHandler(HttpServletRequest req, AccessDeniedException ex) {
        log.warn("[accessDeniedExceptionHandler][userId({}) cannot access url({})]", WebFrameworkUtils.getLoginUserId(req),
                req.getRequestURL(), ex);
        return CommonResult.error(FORBIDDEN);
    }

    /**
     * Handle Guava UncheckedExecutionException
     *
     * For example, cache loading errors; see <a href="https://t.zsxq.com/UszdH">https://t.zsxq.com/UszdH</a>
     */
    @ExceptionHandler(value = UncheckedExecutionException.class)
    public CommonResult<?> uncheckedExecutionExceptionHandler(HttpServletRequest req, UncheckedExecutionException ex) {
        return allExceptionHandler(req, ex.getCause());
    }

    /**
     * Handle business exception ServiceException
     *
     * For example: product out of stock, user mobile already exists.
     */
    @ExceptionHandler(value = ServiceException.class)
    public CommonResult<?> serviceExceptionHandler(ServiceException ex) {
        // only print when not in the ignore set, to avoid excessive ex stack traces
        if (!IGNORE_ERROR_MESSAGES.contains(ex.getMessage())) {
            // even when printing, only print the first StackTraceElement, and use warn level to make it easier to spot
            try {
                StackTraceElement[] stackTraces = ex.getStackTrace();
                for (StackTraceElement stackTrace : stackTraces) {
                    if (ObjUtil.notEqual(stackTrace.getClassName(), ServiceExceptionUtils.class.getName())) {
                        log.warn("[serviceExceptionHandler]\n\t{}", stackTrace);
                        break;
                    }
                }
            } catch (Exception ignored) {
                // ignore log, avoid affecting the main flow
            }
        }
        return CommonResult.error(ex.getCode(), ex.getMessage());
    }

    /**
     * Handle system exception, fallback for everything
     */
    @ExceptionHandler(value = Exception.class)
    public CommonResult<?> defaultExceptionHandler(HttpServletRequest req, Throwable ex) {
        // special case: if the cause is a ServiceException, return directly
        // for example: https://gitee.com/zhijiantianya/yudao-cloud/issues/ICSSRM, https://gitee.com/zhijiantianya/yudao-cloud/issues/ICT6FM
        if (ex.getCause() != null && ex.getCause() instanceof ServiceException) {
            return serviceExceptionHandler((ServiceException) ex.getCause());
        }

        // case 1: handle table-not-exists exceptions
        CommonResult<?> tableNotExistsResult = handleTableNotExists(ex);
        if (tableNotExistsResult != null) {
            return tableNotExistsResult;
        }

        // case 2: handle exception
        log.error("[defaultExceptionHandler]", ex);
        // insert exception log
        createExceptionLog(req, ex);
        // return ERROR CommonResult
        return CommonResult.error(INTERNAL_SERVER_ERROR.getCode(), INTERNAL_SERVER_ERROR.getMsg());
    }

    private void createExceptionLog(HttpServletRequest req, Throwable e) {
        // insert error log
        ApiErrorLogCreateRpcRequest errorLog = new ApiErrorLogCreateRpcRequest();
        try {
            // initialize errorLog
            buildExceptionLog(errorLog, req, e);
            // execute insert errorLog
            apiErrorLogApi.createApiErrorLogAsync(errorLog);
        } catch (Throwable th) {
            log.error("[createExceptionLog][url({}) log({}) exception occurred]", req.getRequestURI(),  JsonUtils.toJsonString(errorLog), th);
        }
    }

    private void buildExceptionLog(ApiErrorLogCreateRpcRequest errorLog, HttpServletRequest request, Throwable e) {
        // handle user info
        errorLog.setUserId(WebFrameworkUtils.getLoginUserId(request));
        errorLog.setUserType(WebFrameworkUtils.getLoginUserType(request));
        // set exception fields
        errorLog.setExceptionName(e.getClass().getName());
        errorLog.setExceptionMessage(ExceptionUtil.getMessage(e));
        errorLog.setExceptionRootCauseMessage(ExceptionUtil.getRootCauseMessage(e));
        errorLog.setExceptionStackTrace(ExceptionUtil.stacktraceToString(e));
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        Assert.notEmpty(stackTraceElements, "exception stackTraceElements must not be empty");
        StackTraceElement stackTraceElement = stackTraceElements[0];
        errorLog.setExceptionClassName(stackTraceElement.getClassName());
        errorLog.setExceptionFileName(stackTraceElement.getFileName());
        errorLog.setExceptionMethodName(stackTraceElement.getMethodName());
        errorLog.setExceptionLineNumber(stackTraceElement.getLineNumber());
        // set other fields
        errorLog.setTraceId(TracerUtils.getTraceId());
        errorLog.setApplicationName(applicationName);
        errorLog.setRequestUrl(request.getRequestURI());
        Map<String, Object> requestParams = MapUtil.<String, Object>builder()
                .put("query", ServletUtils.getParamMap(request))
                .put("body", ServletUtils.getBody(request)).build();
        errorLog.setRequestParams(JsonUtils.toJsonString(requestParams));
        errorLog.setRequestMethod(request.getMethod());
        errorLog.setUserAgent(ServletUtils.getUserAgent(request));
        errorLog.setUserIp(ServletUtils.getClientIP(request));
        errorLog.setExceptionTime(LocalDateTime.now());
    }

    /**
     * Handle Table-not-exists exception cases
     *
     * @param ex exception
     * @return corresponding CommonResult if it is a Table-not-exists exception
     */
    private CommonResult<?> handleTableNotExists(Throwable ex) {
        String message = ExceptionUtil.getRootCauseMessage(ex);
        if (!message.contains("doesn't exist")) {
            return null;
        }
        // 1. Report module
        if (message.contains("report_")) {
            log.error("[Report module yudao-module-report - table schema not imported][see https://cloud.example.com/report/ to enable]");
            return CommonResult.error(NOT_IMPLEMENTED.getCode(),
                    "[Report module yudao-module-report - table schema not imported][see https://cloud.example.com/report/ to enable]");
        }
        // 2. Workflow module
        if (message.contains("bpm_")) {
            log.error("[Workflow module yudao-module-bpm - table schema not imported][see https://cloud.example.com/bpm/ to enable]");
            return CommonResult.error(NOT_IMPLEMENTED.getCode(),
                    "[Workflow module yudao-module-bpm - table schema not imported][see https://cloud.example.com/bpm/ to enable]");
        }
        // 3. WeChat MP
        if (message.contains("mp_")) {
            log.error("[WeChat MP yudao-module-mp - table schema not imported][see https://cloud.example.com/mp/build/ to enable]");
            return CommonResult.error(NOT_IMPLEMENTED.getCode(),
                    "[WeChat MP yudao-module-mp - table schema not imported][see https://cloud.example.com/mp/build/ to enable]");
        }
        // 4. Mall system
        if (StrUtil.containsAny(message, "product_", "promotion_", "trade_")) {
            log.error("[Mall system yudao-module-mall - is disabled][see https://cloud.example.com/mall/build/ to enable]");
            return CommonResult.error(NOT_IMPLEMENTED.getCode(),
                    "[Mall system yudao-module-mall - is disabled][see https://cloud.example.com/mall/build/ to enable]");
        }
        // 5. ERP system
        if (message.contains("erp_")) {
            log.error("[ERP system yudao-module-erp - table schema not imported][see https://cloud.example.com/erp/build/ to enable]");
            return CommonResult.error(NOT_IMPLEMENTED.getCode(),
                    "[ERP system yudao-module-erp - table schema not imported][see https://cloud.example.com/erp/build/ to enable]");
        }
        // 6. CRM system
        if (message.contains("crm_")) {
            log.error("[CRM system yudao-module-crm - table schema not imported][see https://cloud.example.com/crm/build/ to enable]");
            return CommonResult.error(NOT_IMPLEMENTED.getCode(),
                    "[CRM system yudao-module-crm - table schema not imported][see https://cloud.example.com/crm/build/ to enable]");
        }
        // 7. Payment platform
        if (message.contains("pay_")) {
            log.error("[Payment module yudao-module-pay - table schema not imported][see https://cloud.example.com/pay/build/ to enable]");
            return CommonResult.error(NOT_IMPLEMENTED.getCode(),
                    "[Payment module yudao-module-pay - table schema not imported][see https://cloud.example.com/pay/build/ to enable]");
        }
        // 8. AI large model
        if (message.contains("ai_")) {
            log.error("[AI large model yudao-module-ai - table schema not imported][see https://cloud.example.com/ai/build/ to enable]");
            return CommonResult.error(NOT_IMPLEMENTED.getCode(),
                    "[AI large model yudao-module-ai - table schema not imported][see https://cloud.example.com/ai/build/ to enable]");
        }
        // 9. IoT
        if (message.contains("iot_")) {
            log.error("[IoT yudao-module-iot - table schema not imported][see https://www.example.com/iot/build/ to enable]");
            return CommonResult.error(NOT_IMPLEMENTED.getCode(),
                    "[IoT yudao-module-iot - table schema not imported][see https://www.example.com/iot/build/ to enable]");
        }
        return null;
    }

}
