package com.focela.platform.module.infra.repository.mapper.logger;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.infra.controller.admin.logger.dto.apierrorlog.ApiErrorLogPageRequest;
import com.focela.platform.module.infra.repository.entity.logger.ApiErrorLogEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * API 错误日志 Mapper
 *
 * @author 芋道源码
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
     * 物理删除指定时间之前的日志
     *
     * @param createTime 最大时间
     * @param limit      删除条数，防止一次删除太多
     * @return 删除条数
     */
    @Delete("DELETE FROM infra_api_error_log WHERE create_time < #{createTime} LIMIT #{limit}")
    Integer deleteByCreateTimeLt(@Param("createTime") LocalDateTime createTime, @Param("limit") Integer limit);

}
