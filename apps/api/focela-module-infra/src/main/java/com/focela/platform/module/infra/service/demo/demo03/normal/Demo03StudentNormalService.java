package com.focela.platform.module.infra.service.demo.demo03.normal;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.module.infra.controller.admin.demo.demo03.normal.dto.Demo03StudentNormalPageRequest;
import com.focela.platform.module.infra.controller.admin.demo.demo03.normal.dto.Demo03StudentNormalSaveRequest;
import com.focela.platform.module.infra.repository.entity.demo.demo03.Demo03CourseEntity;
import com.focela.platform.module.infra.repository.entity.demo.demo03.Demo03GradeEntity;
import com.focela.platform.module.infra.repository.entity.demo.demo03.Demo03StudentEntity;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 学生 Service 接口
 *
 * @author 芋道源码
 */
public interface Demo03StudentNormalService {

    /**
     * 创建学生
     *
     * @param createRequest 创建信息
     * @return 编号
     */
    Long createDemo03Student(@Valid Demo03StudentNormalSaveRequest createRequest);

    /**
     * 更新学生
     *
     * @param updateRequest 更新信息
     */
    void updateDemo03Student(@Valid Demo03StudentNormalSaveRequest updateRequest);

    /**
     * 删除学生
     *
     * @param id 编号
     */
    void deleteDemo03Student(Long id);

    /**
     * 批量删除学生
     *
     * @param ids 编号
     */
    void deleteDemo03StudentList(List<Long> ids);

    /**
     * 获得学生
     *
     * @param id 编号
     * @return 学生
     */
    Demo03StudentEntity getDemo03Student(Long id);

    /**
     * 获得学生分页
     *
     * @param pageRequest 分页查询
     * @return 学生分页
     */
    PageResult<Demo03StudentEntity> getDemo03StudentPage(Demo03StudentNormalPageRequest pageRequest);

    // ==================== 子表（学生课程） ====================

    /**
     * 获得学生课程列表
     *
     * @param studentId 学生编号
     * @return 学生课程列表
     */
    List<Demo03CourseEntity> getDemo03CourseListByStudentId(Long studentId);

    // ==================== 子表（学生班级） ====================

    /**
     * 获得学生班级
     *
     * @param studentId 学生编号
     * @return 学生班级
     */
    Demo03GradeEntity getDemo03GradeByStudentId(Long studentId);

}