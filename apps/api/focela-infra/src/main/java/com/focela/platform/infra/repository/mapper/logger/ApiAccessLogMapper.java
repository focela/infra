package com.focela.platform.infra.repository.mapper.logger;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.infra.controller.admin.logger.request.accesslog.ApiAccessLogPageRequest;
import com.focela.platform.infra.domain.entity.logger.ApiAccessLogEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * API access log Mapper
 */
@Mapper
public interface ApiAccessLogMapper extends BaseMapperX<ApiAccessLogEntity> {

    default PageResult<ApiAccessLogEntity> selectPage(ApiAccessLogPageRequest request) {
        return selectPage(request, new LambdaQueryWrapperX<ApiAccessLogEntity>()
                .eqIfPresent(ApiAccessLogEntity::getUserId, request.getUserId())
                .eqIfPresent(ApiAccessLogEntity::getUserType, request.getUserType())
                .eqIfPresent(ApiAccessLogEntity::getApplicationName, request.getApplicationName())
                .likeIfPresent(ApiAccessLogEntity::getRequestUrl, request.getRequestUrl())
                .betweenIfPresent(ApiAccessLogEntity::getBeginTime, request.getBeginTime())
                .geIfPresent(ApiAccessLogEntity::getDuration, request.getDuration())
                .eqIfPresent(ApiAccessLogEntity::getResultCode, request.getResultCode())
                .orderByDesc(ApiAccessLogEntity::getId)
        );
    }

    /**
     * Physically delete logs before the specified time
     *
     * @param createTime maximum time
     * @param limit      delete count, to prevent deleting too many at once
     * @return delete count
     */
    @Delete("DELETE FROM infra_api_access_log WHERE create_time < #{createTime} LIMIT #{limit}")
    Integer deleteByCreateTimeLt(@Param("createTime") LocalDateTime createTime, @Param("limit") Integer limit);

}
