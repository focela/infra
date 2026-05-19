package com.focela.platform.infra.repository.mapper.job;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.infra.controller.admin.job.dto.log.JobLogPageRequest;
import com.focela.platform.infra.domain.entity.job.JobLogEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * Job log Mapper
 */
@Mapper
public interface JobLogMapper extends BaseMapperX<JobLogEntity> {

    default PageResult<JobLogEntity> selectPage(JobLogPageRequest request) {
        return selectPage(request, new LambdaQueryWrapperX<JobLogEntity>()
                .eqIfPresent(JobLogEntity::getJobId, request.getJobId())
                .likeIfPresent(JobLogEntity::getHandlerName, request.getHandlerName())
                .geIfPresent(JobLogEntity::getBeginTime, request.getBeginTime())
                .leIfPresent(JobLogEntity::getEndTime, request.getEndTime())
                .eqIfPresent(JobLogEntity::getStatus, request.getStatus())
                .orderByDesc(JobLogEntity::getId) // ID descending
        );
    }

    /**
     * Physically delete logs before the specified time
     *
     * @param createTime maximum time
     * @param limit      delete count, to prevent deleting too many at once
     * @return delete count
     */
    @Delete("DELETE FROM infra_job_log WHERE create_time < #{createTime} LIMIT #{limit}")
    Integer deleteByCreateTimeLt(@Param("createTime") LocalDateTime createTime, @Param("limit") Integer limit);

}
