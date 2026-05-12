package com.focela.platform.module.infra.service.demo.demo03.erp;

import cn.hutool.core.collection.CollUtil;
import com.focela.platform.framework.common.model.PageParam;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.module.infra.controller.admin.demo.demo03.erp.dto.Demo03StudentErpPageRequest;
import com.focela.platform.module.infra.controller.admin.demo.demo03.erp.dto.Demo03StudentErpSaveRequest;
import com.focela.platform.module.infra.repository.entity.demo.demo03.Demo03CourseEntity;
import com.focela.platform.module.infra.repository.entity.demo.demo03.Demo03GradeEntity;
import com.focela.platform.module.infra.repository.entity.demo.demo03.Demo03StudentEntity;
import com.focela.platform.module.infra.repository.mapper.demo.demo03.erp.Demo03CourseErpMapper;
import com.focela.platform.module.infra.repository.mapper.demo.demo03.erp.Demo03GradeErpMapper;
import com.focela.platform.module.infra.repository.mapper.demo.demo03.erp.Demo03StudentErpMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.focela.platform.framework.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.module.infra.enums.ErrorCodeConstants.*;

/**
 * 学生 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class Demo03StudentErpServiceImpl implements Demo03StudentErpService {

    @Resource
    private Demo03StudentErpMapper demo03StudentErpMapper;
    @Resource
    private Demo03CourseErpMapper demo03CourseErpMapper;
    @Resource
    private Demo03GradeErpMapper demo03GradeErpMapper;

    @Override
    public Long createDemo03Student(Demo03StudentErpSaveRequest createRequest) {
        // 插入
        Demo03StudentEntity demo03Student = BeanUtils.toBean(createRequest, Demo03StudentEntity.class);
        demo03StudentErpMapper.insert(demo03Student);
        // 返回
        return demo03Student.getId();
    }

    @Override
    public void updateDemo03Student(Demo03StudentErpSaveRequest updateRequest) {
        // 校验存在
        validateDemo03StudentExists(updateRequest.getId());
        // 更新
        Demo03StudentEntity updateObj = BeanUtils.toBean(updateRequest, Demo03StudentEntity.class);
        demo03StudentErpMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDemo03Student(Long id) {
        // 校验存在
        validateDemo03StudentExists(id);
        // 删除
        demo03StudentErpMapper.deleteById(id);

        // 删除子表
        deleteDemo03CourseByStudentId(id);
        deleteDemo03GradeByStudentId(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDemo03StudentList(List<Long> ids) {
        // 校验存在
        validateDemo03StudentExists(ids);
        // 删除
        demo03StudentErpMapper.deleteByIds(ids);

        // 删除子表
        deleteDemo03CourseByStudentIds(ids);
        deleteDemo03GradeByStudentIds(ids);
    }

    private void validateDemo03StudentExists(List<Long> ids) {
        List<Demo03StudentEntity> list = demo03StudentErpMapper.selectByIds(ids);
        if (CollUtil.isEmpty(list) || list.size() != ids.size()) {
            throw exception(DEMO03_STUDENT_NOT_EXISTS);
        }
    }

    private void validateDemo03StudentExists(Long id) {
        if (demo03StudentErpMapper.selectById(id) == null) {
            throw exception(DEMO03_STUDENT_NOT_EXISTS);
        }
    }

    @Override
    public Demo03StudentEntity getDemo03Student(Long id) {
        return demo03StudentErpMapper.selectById(id);
    }

    @Override
    public PageResult<Demo03StudentEntity> getDemo03StudentPage(Demo03StudentErpPageRequest pageRequest) {
        return demo03StudentErpMapper.selectPage(pageRequest);
    }

    // ==================== 子表（学生课程） ====================

    @Override
    public PageResult<Demo03CourseEntity> getDemo03CoursePage(PageParam pageRequest, Long studentId) {
        return demo03CourseErpMapper.selectPage(pageRequest, studentId);
    }

    @Override
    public Long createDemo03Course(Demo03CourseEntity demo03Course) {
        demo03CourseErpMapper.insert(demo03Course);
        return demo03Course.getId();
    }

    @Override
    public void updateDemo03Course(Demo03CourseEntity demo03Course) {
        // 校验存在
        validateDemo03CourseExists(demo03Course.getId());
        // 更新
        demo03Course.clean();
        demo03CourseErpMapper.updateById(demo03Course);
    }

    @Override
    public void deleteDemo03Course(Long id) {
        // 删除
        demo03CourseErpMapper.deleteById(id);
    }

    @Override
    public void deleteDemo03CourseList(List<Long> ids) {
        // 删除
        demo03CourseErpMapper.deleteByIds(ids);
    }

    @Override
    public Demo03CourseEntity getDemo03Course(Long id) {
        return demo03CourseErpMapper.selectById(id);
    }

    private void validateDemo03CourseExists(Long id) {
        if (demo03CourseErpMapper.selectById(id) == null) {
            throw exception(DEMO03_COURSE_NOT_EXISTS);
        }
    }

    private void deleteDemo03CourseByStudentId(Long studentId) {
        demo03CourseErpMapper.deleteByStudentId(studentId);
    }

    private void deleteDemo03CourseByStudentIds(List<Long> studentIds) {
        demo03CourseErpMapper.deleteByStudentIds(studentIds);
    }

    // ==================== 子表（学生班级） ====================

    @Override
    public PageResult<Demo03GradeEntity> getDemo03GradePage(PageParam pageRequest, Long studentId) {
        return demo03GradeErpMapper.selectPage(pageRequest, studentId);
    }

    @Override
    public Long createDemo03Grade(Demo03GradeEntity demo03Grade) {
        // 校验是否已经存在
        if (demo03GradeErpMapper.selectByStudentId(demo03Grade.getStudentId()) != null) {
            throw exception(DEMO03_GRADE_EXISTS);
        }
        // 插入
        demo03GradeErpMapper.insert(demo03Grade);
        return demo03Grade.getId();
    }

    @Override
    public void updateDemo03Grade(Demo03GradeEntity demo03Grade) {
        // 校验存在
        validateDemo03GradeExists(demo03Grade.getId());
        // 更新
        demo03Grade.clean();
        demo03GradeErpMapper.updateById(demo03Grade);
    }

    @Override
    public void deleteDemo03Grade(Long id) {
        // 删除
        demo03GradeErpMapper.deleteById(id);
    }

    @Override
    public void deleteDemo03GradeList(List<Long> ids) {
        // 删除
        demo03GradeErpMapper.deleteByIds(ids);
    }

    @Override
    public Demo03GradeEntity getDemo03Grade(Long id) {
        return demo03GradeErpMapper.selectById(id);
    }

    private void validateDemo03GradeExists(Long id) {
        if (demo03GradeErpMapper.selectById(id) == null) {
            throw exception(DEMO03_GRADE_NOT_EXISTS);
        }
    }

    private void deleteDemo03GradeByStudentId(Long studentId) {
        demo03GradeErpMapper.deleteByStudentId(studentId);
    }

    private void deleteDemo03GradeByStudentIds(List<Long> studentIds) {
        demo03GradeErpMapper.deleteByStudentIds(studentIds);
    }

}