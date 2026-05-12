package com.focela.platform.module.infra.service.demo.demo03.inner;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.module.infra.controller.admin.demo.demo03.inner.dto.Demo03StudentInnerPageRequest;
import com.focela.platform.module.infra.controller.admin.demo.demo03.inner.dto.Demo03StudentInnerSaveRequest;
import com.focela.platform.module.infra.repository.entity.demo.demo03.Demo03CourseEntity;
import com.focela.platform.module.infra.repository.entity.demo.demo03.Demo03GradeEntity;
import com.focela.platform.module.infra.repository.entity.demo.demo03.Demo03StudentEntity;
import com.focela.platform.module.infra.repository.mapper.demo.demo03.inner.Demo03CourseInnerMapper;
import com.focela.platform.module.infra.repository.mapper.demo.demo03.inner.Demo03GradeInnerMapper;
import com.focela.platform.module.infra.repository.mapper.demo.demo03.inner.Demo03StudentInnerMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.focela.platform.framework.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.framework.common.utils.collection.CollectionUtils.convertList;
import static com.focela.platform.framework.common.utils.collection.CollectionUtils.diffList;
import static com.focela.platform.module.infra.enums.ErrorCodeConstants.DEMO03_STUDENT_NOT_EXISTS;

/**
 * 学生 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class Demo03StudentInnerServiceImpl implements Demo03StudentInnerService {

    @Resource
    private Demo03StudentInnerMapper demo03StudentInnerMapper;
    @Resource
    private Demo03CourseInnerMapper demo03CourseInnerMapper;
    @Resource
    private Demo03GradeInnerMapper demo03GradeInnerMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDemo03Student(Demo03StudentInnerSaveRequest createRequest) {
        // 插入
        Demo03StudentEntity demo03Student = BeanUtils.toBean(createRequest, Demo03StudentEntity.class);
        demo03StudentInnerMapper.insert(demo03Student);

        // 插入子表
        createDemo03CourseList(demo03Student.getId(), createRequest.getDemo03Courses());
        createDemo03Grade(demo03Student.getId(), createRequest.getDemo03Grade());
        // 返回
        return demo03Student.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDemo03Student(Demo03StudentInnerSaveRequest updateRequest) {
        // 校验存在
        validateDemo03StudentExists(updateRequest.getId());
        // 更新
        Demo03StudentEntity updateObj = BeanUtils.toBean(updateRequest, Demo03StudentEntity.class);
        demo03StudentInnerMapper.updateById(updateObj);

        // 更新子表
        updateDemo03CourseList(updateRequest.getId(), updateRequest.getDemo03Courses());
        updateDemo03Grade(updateRequest.getId(), updateRequest.getDemo03Grade());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDemo03Student(Long id) {
        // 校验存在
        validateDemo03StudentExists(id);
        // 删除
        demo03StudentInnerMapper.deleteById(id);

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
        demo03StudentInnerMapper.deleteByIds(ids);

        // 删除子表
        deleteDemo03CourseByStudentIds(ids);
        deleteDemo03GradeByStudentIds(ids);
    }

    private void validateDemo03StudentExists(List<Long> ids) {
        List<Demo03StudentEntity> list = demo03StudentInnerMapper.selectByIds(ids);
        if (CollUtil.isEmpty(list) || list.size() != ids.size()) {
            throw exception(DEMO03_STUDENT_NOT_EXISTS);
        }
    }

    private void validateDemo03StudentExists(Long id) {
        if (demo03StudentInnerMapper.selectById(id) == null) {
            throw exception(DEMO03_STUDENT_NOT_EXISTS);
        }
    }

    @Override
    public Demo03StudentEntity getDemo03Student(Long id) {
        return demo03StudentInnerMapper.selectById(id);
    }

    @Override
    public PageResult<Demo03StudentEntity> getDemo03StudentPage(Demo03StudentInnerPageRequest pageRequest) {
        return demo03StudentInnerMapper.selectPage(pageRequest);
    }

    // ==================== 子表（学生课程） ====================

    @Override
    public List<Demo03CourseEntity> getDemo03CourseListByStudentId(Long studentId) {
        return demo03CourseInnerMapper.selectListByStudentId(studentId);
    }

    private void createDemo03CourseList(Long studentId, List<Demo03CourseEntity> list) {
        list.forEach(o -> o.setStudentId(studentId).clean());
        demo03CourseInnerMapper.insertBatch(list);
    }

    private void updateDemo03CourseList(Long studentId, List<Demo03CourseEntity> list) {
        list.forEach(o -> o.setStudentId(studentId).clean());
        List<Demo03CourseEntity> oldList = demo03CourseInnerMapper.selectListByStudentId(studentId);
        List<List<Demo03CourseEntity>> diffList = diffList(oldList, list, (oldVal, newVal) -> {
            boolean same = ObjectUtil.equal(oldVal.getId(), newVal.getId());
            if (same) {
                newVal.setId(oldVal.getId());
            }
            return same;
        });

        // 第二步，批量添加、修改、删除
        if (CollUtil.isNotEmpty(diffList.get(0))) {
            demo03CourseInnerMapper.insertBatch(diffList.get(0));
        }
        if (CollUtil.isNotEmpty(diffList.get(1))) {
            demo03CourseInnerMapper.updateBatch(diffList.get(1));
        }
        if (CollUtil.isNotEmpty(diffList.get(2))) {
            demo03CourseInnerMapper.deleteByIds(convertList(diffList.get(2), Demo03CourseEntity::getId));
        }
    }

    private void deleteDemo03CourseByStudentId(Long studentId) {
        demo03CourseInnerMapper.deleteByStudentId(studentId);
    }

    private void deleteDemo03CourseByStudentIds(List<Long> studentIds) {
        demo03CourseInnerMapper.deleteByStudentIds(studentIds);
    }

    // ==================== 子表（学生班级） ====================

    @Override
    public Demo03GradeEntity getDemo03GradeByStudentId(Long studentId) {
        return demo03GradeInnerMapper.selectByStudentId(studentId);
    }

    private void createDemo03Grade(Long studentId, Demo03GradeEntity demo03Grade) {
        if (demo03Grade == null) {
            return;
        }
        demo03Grade.setStudentId(studentId);
        demo03GradeInnerMapper.insert(demo03Grade);
    }

    private void updateDemo03Grade(Long studentId, Demo03GradeEntity demo03Grade) {
        if (demo03Grade == null) {
            return;
        }
        demo03Grade.setStudentId(studentId).clean();
        demo03GradeInnerMapper.insertOrUpdate(demo03Grade);
    }

    private void deleteDemo03GradeByStudentId(Long studentId) {
        demo03GradeInnerMapper.deleteByStudentId(studentId);
    }

    private void deleteDemo03GradeByStudentIds(List<Long> studentIds) {
        demo03GradeInnerMapper.deleteByStudentIds(studentIds);
    }

}