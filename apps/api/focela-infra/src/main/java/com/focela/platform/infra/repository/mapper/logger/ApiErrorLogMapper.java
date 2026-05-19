package com.focela.platform.infra.repository.mapper.logger;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.infra.controller.admin.logger.dto.apierrorlog.ApiErrorLogPageRequest;
import com.focela.platform.infra.domain.entity.logger.ApiErrorLogEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * API error log Mapper
 */
@Mapper
public interface ApiErrorLogMapper extends BaseMapperX<ApiErrorLogEntity> {

    default PageResult<ApiErrorLogEntity> selectPage(ApiErrorLogPageRequest request) {
        return selectPage(request, new LambdaQueryWrapperX<ApiErrorLogEntity>()
                .eqIfPresent(ApiErrorLogEntity::getUserId, request.getUserId())
                .eqIfPresent(ApiErrorLogEntity::getUserType, request.getUserType())
                .eqIfPresent(ApiErrorLogEntity::getApplicationName, request.getApplicationName())
                .likeIfPresent(ApiErrorLogEntity::getRequestUrl, request.getRequestUrl())
                .betweenIfPresent(ApiErrorLogEntity::getExceptionTime, request.getExceptionTime())
                .eqIfPresent(ApiErrorLogEntity::getProcessStatus, request.getProcessStatus())
                .orderByDesc(ApiErrorLogEntity::getId)
        );
    }

    /**
     * Physically delete logs before the specified time
     *
     * @param createTime maximum time
     * @param limit      delete count, to prevent deleting too many at once
     * @return delete count
     */
    @Delete("DELETE FROM infra_api_error_log WHERE create_time < #{createTime} LIMIT #{limit}")
    Integer deleteByCreateTimeLt(@Param("createTime") LocalDateTime createTime, @Param("limit") Integer limit);

}
