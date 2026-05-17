package com.focela.platform.infra.service.logger;

import com.focela.platform.common.api.infra.logger.dto.ApiAccessLogCreateRpcRequest;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.common.utils.string.StrUtils;
import com.focela.platform.tenant.core.context.TenantContextHolder;
import com.focela.platform.tenant.core.utils.TenantUtils;
import com.focela.platform.infra.controller.admin.logger.dto.apiaccesslog.ApiAccessLogPageRequest;
import com.focela.platform.infra.entity.logger.ApiAccessLogEntity;
import com.focela.platform.infra.repository.mapper.logger.ApiAccessLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

import static com.focela.platform.infra.entity.logger.ApiAccessLogEntity.REQUEST_PARAMS_MAX_LENGTH;
import static com.focela.platform.infra.entity.logger.ApiAccessLogEntity.RESULT_MSG_MAX_LENGTH;

/**
 * Implementation class of the API access log Service
 */
@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class DefaultApiAccessLogService implements ApiAccessLogService {

    private final ApiAccessLogMapper apiAccessLogMapper;

    @Override
    public void createApiAccessLog(ApiAccessLogCreateRpcRequest createRequest) {
        ApiAccessLogEntity apiAccessLog = BeanUtils.toBean(createRequest, ApiAccessLogEntity.class);
        apiAccessLog.setRequestParams(StrUtils.maxLength(apiAccessLog.getRequestParams(), REQUEST_PARAMS_MAX_LENGTH));
        apiAccessLog.setResultMsg(StrUtils.maxLength(apiAccessLog.getResultMsg(), RESULT_MSG_MAX_LENGTH));
        if (TenantContextHolder.getTenantId() != null) {
            apiAccessLogMapper.insert(apiAccessLog);
        } else {
            // In extreme cases the context has no tenant; ignore the tenant context to avoid insert failure.
            TenantUtils.executeIgnore(() -> apiAccessLogMapper.insert(apiAccessLog));
        }
    }

    @Override
    public ApiAccessLogEntity getApiAccessLog(Long id) {
        return apiAccessLogMapper.selectById(id);
    }

    @Override
    public PageResult<ApiAccessLogEntity> getApiAccessLogPage(ApiAccessLogPageRequest pageRequest) {
        return apiAccessLogMapper.selectPage(pageRequest);
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public Integer cleanAccessLog(Integer exceedDay, Integer deleteLimit) {
        int count = 0;
        LocalDateTime expireDate = LocalDateTime.now().minusDays(exceedDay);
        // Delete in a loop until no more matching records remain
        for (int i = 0; i < Short.MAX_VALUE; i++) {
            int deleteCount = apiAccessLogMapper.deleteByCreateTimeLt(expireDate, deleteLimit);
            count += deleteCount;
            // Reached the deletion limit, meaning end of batch
            if (deleteCount < deleteLimit) {
                break;
            }
        }
        return count;
    }

}
