package com.focela.platform.module.infra.repository.mapper.demo.demo03.erp;

import com.focela.platform.framework.common.pojo.PageParam;
import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.infra.repository.entity.demo.demo03.Demo03CourseEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 学生课程 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface Demo03CourseErpMapper extends BaseMapperX<Demo03CourseEntity> {

    default PageResult<Demo03CourseEntity> selectPage(PageParam reqVO, Long studentId) {
        return selectPage(reqVO, new LambdaQueryWrapperX<Demo03CourseEntity>()
                .eq(Demo03CourseEntity::getStudentId, studentId)
                .orderByDesc(Demo03CourseEntity::getId));
    }

    default int deleteByStudentId(Long studentId) {
        return delete(Demo03CourseEntity::getStudentId, studentId);
    }

    default int deleteByStudentIds(List<Long> studentIds) {
        return deleteBatch(Demo03CourseEntity::getStudentId, studentIds);
    }

}
