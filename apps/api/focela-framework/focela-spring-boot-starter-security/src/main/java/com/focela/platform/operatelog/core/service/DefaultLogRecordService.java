package com.focela.platform.operatelog.core.service;

import com.focela.platform.common.api.system.logger.OperateLogContractApi;
import com.focela.platform.common.api.system.logger.dto.OperateLogCreateRpcRequest;
import com.focela.platform.common.utils.monitor.TracerUtils;
import com.focela.platform.common.utils.servlet.ServletUtils;
import com.focela.platform.security.core.LoginUser;
import com.focela.platform.security.core.utils.SecurityFrameworkUtils;
import com.mzt.logapi.beans.LogRecord;
import com.mzt.logapi.service.ILogRecordService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

/**
 * Operate log ILogRecordService implementation.
 *
 * Records operate logs based on {@link OperateLogContractApi}.
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultLogRecordService implements ILogRecordService {

    private final OperateLogContractApi operateLogApi;

    @Override
    public void record(LogRecord logRecord) {
        OperateLogCreateRpcRequest request = new OperateLogCreateRpcRequest();
        try {
            request.setTraceId(TracerUtils.getTraceId());
            // Fill in user information
            fillUserFields(request);
            // Fill in module information
            fillModuleFields(request, logRecord);
            // Fill in request information
            fillRequestFields(request);

            // 2. Record the log asynchronously
            operateLogApi.createOperateLogAsync(request);
        } catch (Throwable ex) {
            // Because this is invoked asynchronously via @Async, log here so issues are easier to trace
            log.error("[record][url({}) log({}) exception occurred]", request.getRequestUrl(), request, ex);
        }
    }

    private static void fillUserFields(OperateLogCreateRpcRequest request) {
        // Use SecurityFrameworkUtils because rpc, mq, and job are not necessarily web contexts.
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            return;
        }
        request.setUserId(loginUser.getId());
        request.setUserType(loginUser.getUserType());
    }

    public static void fillModuleFields(OperateLogCreateRpcRequest request, LogRecord logRecord) {
        request.setType(logRecord.getType()); // Major module type, e.g. CRM customer
        request.setSubType(logRecord.getSubType());// Operation name, e.g. transfer customer
        request.setBizId(Long.parseLong(logRecord.getBizNo())); // Business ID, e.g. customer ID
        request.setAction(logRecord.getAction());// Operation content, e.g. update user with ID 1 - change gender from male to female and update the name.
        request.setExtra(logRecord.getExtra()); // Extra field; some complex business operations need to record extra fields (JSON format), e.g. recording the order ID: { orderId: "1" }
    }

    private static void fillRequestFields(OperateLogCreateRpcRequest createRequest) {
        // Get the Request object
        HttpServletRequest request = ServletUtils.getRequest();
        if (request == null) {
            return;
        }
        // Fill in request information
        createRequest.setRequestMethod(request.getMethod());
        createRequest.setRequestUrl(request.getRequestURI());
        createRequest.setUserIp(ServletUtils.getClientIP(request));
        createRequest.setUserAgent(ServletUtils.getUserAgent(request));
    }

    @Override
    public List<LogRecord> queryLog(String bizNo, String type) {
        throw new UnsupportedOperationException("use OperateLogApi for operation log query");
    }

    @Override
    public List<LogRecord> queryLogByBizNo(String bizNo, String type, String subType) {
        throw new UnsupportedOperationException("use OperateLogApi for operation log query");
    }

}