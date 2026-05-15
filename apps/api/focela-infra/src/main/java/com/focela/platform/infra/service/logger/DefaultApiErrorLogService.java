package com.focela.platform.infra.service.logger;

import com.focela.platform.common.api.infra.logger.dto.ApiErrorLogCreateRpcRequest;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.common.utils.string.StrUtils;
import com.focela.platform.tenant.core.context.TenantContextHolder;
import com.focela.platform.tenant.core.utils.TenantUtils;
import com.focela.platform.infra.controller.admin.logger.dto.apierrorlog.ApiErrorLogPageRequest;
import com.focela.platform.infra.entity.logger.ApiErrorLogEntity;
import com.focela.platform.infra.repository.mapper.logger.ApiErrorLogMapper;
import com.focela.platform.infra.enums.logger.ApiErrorLogProcessStatusEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.infra.entity.logger.ApiErrorLogEntity.REQUEST_PARAMS_MAX_LENGTH;
import static com.focela.platform.infra.constants.ErrorCodeConstants.API_ERROR_LOG_NOT_FOUND;
import static com.focela.platform.infra.constants.ErrorCodeConstants.API_ERROR_LOG_PROCESSED;

/**
 * API 错误日志 Service 实现类
 */
@Service
@Validated
@Slf4j
public class DefaultApiErrorLogService implements ApiErrorLogService {

    @Resource
    private ApiErrorLogMapper apiErrorLogMapper;

    @Override
    public void createApiErrorLog(ApiErrorLogCreateRpcRequest createDTO) {
        ApiErrorLogEntity apiErrorLog = BeanUtils.toBean(createDTO, ApiErrorLogEntity.class)
                .setProcessStatus(ApiErrorLogProcessStatusEnum.INIT.getStatus());
        apiErrorLog.setRequestParams(StrUtils.maxLength(apiErrorLog.getRequestParams(), REQUEST_PARAMS_MAX_LENGTH));
        try {
            if (TenantContextHolder.getTenantId() != null) {
                apiErrorLogMapper.insert(apiErrorLog);
            } else {
                // 极端情况下，上下文中没有租户时，此时忽略租户上下文，避免插入失败！
                TenantUtils.executeIgnore(() -> apiErrorLogMapper.insert(apiErrorLog));
            }
        } catch (Exception ex) {
            // 兜底处理，目前只有 yudao-cloud 会发生：https://gitee.com/yudaocode/yudao-cloud-mini/issues/IC1O0A
            log.error("[createApiErrorLog][记录when ({}) 发生exception]", createDTO, ex);
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
        // 标记处理
        apiErrorLogMapper.updateById(ApiErrorLogEntity.builder().id(id).processStatus(processStatus)
                .processUserId(processUserId).processTime(LocalDateTime.now()).build());
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public Integer cleanErrorLog(Integer exceedDay, Integer deleteLimit) {
        int count = 0;
        LocalDateTime expireDate = LocalDateTime.now().minusDays(exceedDay);
        // 循环删除，直到没有满足条件的数据
        for (int i = 0; i < Short.MAX_VALUE; i++) {
            int deleteCount = apiErrorLogMapper.deleteByCreateTimeLt(expireDate, deleteLimit);
            count += deleteCount;
            // 达到删除预期条数，说明到底了
            if (deleteCount < deleteLimit) {
                break;
            }
        }
        return count;
    }

}
