package com.focela.platform.infra.service.logger;

import com.focela.platform.common.api.infra.logger.dto.ApiErrorLogCreateRpcRequest;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.common.utils.string.StrUtils;
import com.focela.platform.tenant.core.context.TenantContextHolder;
import com.focela.platform.tenant.core.utils.TenantUtils;
import com.focela.platform.infra.controller.admin.logger.request.errorlog.ApiErrorLogPageRequest;
import com.focela.platform.infra.domain.entity.logger.ApiErrorLogEntity;
import com.focela.platform.infra.repository.mapper.logger.ApiErrorLogMapper;
import com.focela.platform.infra.enums.logger.ApiErrorLogProcessStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.infra.domain.entity.logger.ApiErrorLogEntity.REQUEST_PARAMS_MAX_LENGTH;
import static com.focela.platform.infra.constants.InfraErrorCodeConstants.API_ERROR_LOG_NOT_FOUND;
import static com.focela.platform.infra.constants.InfraErrorCodeConstants.API_ERROR_LOG_PROCESSED;

/**
 * Implementation class of the API error log Service
 */
@Service
@Validated
@Slf4j
@RequiredArgsConstructor
public class DefaultApiErrorLogService implements ApiErrorLogService {

    private final ApiErrorLogMapper apiErrorLogMapper;

    @Override
    public void createApiErrorLog(ApiErrorLogCreateRpcRequest createRequest) {
        ApiErrorLogEntity apiErrorLog = BeanUtils.toBean(createRequest, ApiErrorLogEntity.class)
                .setProcessStatus(ApiErrorLogProcessStatusEnum.INIT.getStatus());
        apiErrorLog.setRequestParams(StrUtils.maxLength(apiErrorLog.getRequestParams(), REQUEST_PARAMS_MAX_LENGTH));
        try {
            if (TenantContextHolder.getTenantId() != null) {
                apiErrorLogMapper.insert(apiErrorLog);
            } else {
                // In extreme cases the context has no tenant; ignore the tenant context to avoid insert failure.
                TenantUtils.executeIgnore(() -> apiErrorLogMapper.insert(apiErrorLog));
            }
        } catch (Exception ex) {
            // Fallback handling.
            log.error("[createApiErrorLog][record ({}) raised exception]", createRequest, ex);
        }
    }

    @Override
    public PageResult<ApiErrorLogEntity> getApiErrorLogPage(ApiErrorLogPageRequest pageRequest) {
        return apiErrorLogMapper.selectPage(pageRequest);
    }

    @Override
    public ApiErrorLogEntity getApiErrorLog(Long id) {
        return apiErrorLogMapper.selectById(id);
    }

    @Override
    public void updateApiErrorLogProcess(Long id, Integer processStatus, Long processUserId) {
        ApiErrorLogEntity errorLog = apiErrorLogMapper.selectById(id);
        if (errorLog == null) {
            throw exception(API_ERROR_LOG_NOT_FOUND);
        }
        if (!ApiErrorLogProcessStatusEnum.INIT.getStatus().equals(errorLog.getProcessStatus())) {
            throw exception(API_ERROR_LOG_PROCESSED);
        }
        // Mark as processed
        apiErrorLogMapper.updateById(ApiErrorLogEntity.builder().id(id).processStatus(processStatus)
                .processUserId(processUserId).processTime(LocalDateTime.now()).build());
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public Integer cleanErrorLog(Integer exceedDay, Integer deleteLimit) {
        int count = 0;
        LocalDateTime expireDate = LocalDateTime.now().minusDays(exceedDay);
        // Delete in a loop until no more matching records remain
        for (int i = 0; i < Short.MAX_VALUE; i++) {
            int deleteCount = apiErrorLogMapper.deleteByCreateTimeLt(expireDate, deleteLimit);
            count += deleteCount;
            // Reached the deletion limit, meaning end of batch
            if (deleteCount < deleteLimit) {
                break;
            }
        }
        return count;
    }

}
