package com.focela.platform.module.infra.repository.mapper.demo.demo03.normal;

import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.module.infra.repository.entity.demo.demo03.Demo03CourseEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 学生课程 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface Demo03CourseNormalMapper extends BaseMapperX<Demo03CourseEntity> {

    default List<Demo03CourseEntity> selectListByStudentId(Long studentId) {
        return selectList(Demo03CourseEntity::getStudentId, studentId);
    }

    default int deleteByStudentId(Long studentId) {
        return delete(Demo03CourseEntity::getStudentId, studentId);
    }

    default int deleteByStudentIds(List<Long> studentIds) {
        return deleteBatch(Demo03CourseEntity::getStudentId, studentIds);
    }

}
