package com.focela.platform.module.infra.repository.mapper.demo.demo03.erp;

import com.focela.platform.framework.common.model.PageParam;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.infra.repository.entity.demo.demo03.Demo03GradeEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 学生班级 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface Demo03GradeErpMapper extends BaseMapperX<Demo03GradeEntity> {

    default PageResult<Demo03GradeEntity> selectPage(PageParam request, Long studentId) {
        return selectPage(request, new LambdaQueryWrapperX<Demo03GradeEntity>()
                .eq(Demo03GradeEntity::getStudentId, studentId)
                .orderByDesc(Demo03GradeEntity::getId));
    }

    default Demo03GradeEntity selectByStudentId(Long studentId) {
        return selectOne(Demo03GradeEntity::getStudentId, studentId);
    }

    default int deleteByStudentId(Long studentId) {
        return delete(Demo03GradeEntity::getStudentId, studentId);
    }

    default int deleteByStudentIds(List<Long> studentIds) {
        return deleteBatch(Demo03GradeEntity::getStudentId, studentIds);
    }

}
