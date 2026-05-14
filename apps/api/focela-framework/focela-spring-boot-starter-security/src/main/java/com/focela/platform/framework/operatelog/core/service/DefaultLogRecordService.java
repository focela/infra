package com.focela.platform.framework.operatelog.core.service;

import com.focela.platform.framework.common.contract.system.logger.OperateLogContractApi;
import com.focela.platform.framework.common.contract.system.logger.dto.OperateLogCreateRpcRequest;
import com.focela.platform.framework.common.utils.monitor.TracerUtils;
import com.focela.platform.framework.common.utils.servlet.ServletUtils;
import com.focela.platform.framework.security.core.LoginUser;
import com.focela.platform.framework.security.core.utils.SecurityFrameworkUtils;
import com.mzt.logapi.beans.LogRecord;
import com.mzt.logapi.service.ILogRecordService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Operate log ILogRecordService implementation.
 *
 * Records operate logs based on {@link OperateLogContractApi}.
 */
@Slf4j
public class DefaultLogRecordService implements ILogRecordService {

    @Resource
    private OperateLogContractApi operateLogApi;

    @Override
    public void record(LogRecord logRecord) {
        OperateLogCreateRpcRequest reqDTO = new OperateLogCreateRpcRequest();
        try {
            reqDTO.setTraceId(TracerUtils.getTraceId());
            // Fill in user information
            fillUserFields(reqDTO);
            // Fill in module information
            fillModuleFields(reqDTO, logRecord);
            // Fill in request information
            fillRequestFields(reqDTO);

            // 2. Record the log asynchronously
            operateLogApi.createOperateLogAsync(reqDTO);
        } catch (Throwable ex) {
            // Because this is invoked asynchronously via @Async, log here so issues are easier to trace
            log.error("[record][url({}) log({}) exception occurred]", reqDTO.getRequestUrl(), reqDTO, ex);
        }
    }

    private static void fillUserFields(OperateLogCreateRpcRequest reqDTO) {
        // Use SecurityFrameworkUtils because rpc, mq, and job are not necessarily web contexts.
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            return;
        }
        reqDTO.setUserId(loginUser.getId());
        reqDTO.setUserType(loginUser.getUserType());
    }

    public static void fillModuleFields(OperateLogCreateRpcRequest reqDTO, LogRecord logRecord) {
        reqDTO.setType(logRecord.getType()); // Major module type, e.g. CRM customer
        reqDTO.setSubType(logRecord.getSubType());// Operation name, e.g. transfer customer
        reqDTO.setBizId(Long.parseLong(logRecord.getBizNo())); // Business ID, e.g. customer ID
        reqDTO.setAction(logRecord.getAction());// Operation content, e.g. update user with ID 1 - change gender from male to female and update the name.
        reqDTO.setExtra(logRecord.getExtra()); // Extra field; some complex business operations need to record extra fields (JSON format), e.g. recording the order ID: { orderId: "1" }
    }

    private static void fillRequestFields(OperateLogCreateRpcRequest reqDTO) {
        // Get the Request object
        HttpServletRequest request = ServletUtils.getRequest();
        if (request == null) {
            return;
        }
        // Fill in request information
        reqDTO.setRequestMethod(request.getMethod());
        reqDTO.setRequestUrl(request.getRequestURI());
        reqDTO.setUserIp(ServletUtils.getClientIP(request));
        reqDTO.setUserAgent(ServletUtils.getUserAgent(request));
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