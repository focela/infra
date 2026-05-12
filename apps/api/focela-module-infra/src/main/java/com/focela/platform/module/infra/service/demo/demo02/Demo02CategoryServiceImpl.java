package com.focela.platform.module.infra.service.demo.demo02;

import com.focela.platform.framework.common.util.object.BeanUtils;
import com.focela.platform.module.infra.controller.admin.demo.demo02.dto.Demo02CategoryListRequest;
import com.focela.platform.module.infra.controller.admin.demo.demo02.dto.Demo02CategorySaveRequest;
import com.focela.platform.module.infra.repository.entity.demo.demo02.Demo02CategoryEntity;
import com.focela.platform.module.infra.repository.mapper.demo.demo02.Demo02CategoryMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;

import static com.focela.platform.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.focela.platform.module.infra.enums.ErrorCodeConstants.*;

/**
 * 示例分类 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class Demo02CategoryServiceImpl implements Demo02CategoryService {

    @Resource
    private Demo02CategoryMapper demo02CategoryMapper;

    @Override
    public Long createDemo02Category(Demo02CategorySaveRequest createRequest) {
        // 校验父级编号的有效性
        validateParentDemo02Category(null, createRequest.getParentId());
        // 校验名字的唯一性
        validateDemo02CategoryNameUnique(null, createRequest.getParentId(), createRequest.getName());

        // 插入
        Demo02CategoryEntity demo02Category = BeanUtils.toBean(createRequest, Demo02CategoryEntity.class);
        demo02CategoryMapper.insert(demo02Category);
        // 返回
        return demo02Category.getId();
    }

    @Override
    public void updateDemo02Category(Demo02CategorySaveRequest updateRequest) {
        // 校验存在
        validateDemo02CategoryExists(updateRequest.getId());
        // 校验父级编号的有效性
        validateParentDemo02Category(updateRequest.getId(), updateRequest.getParentId());
        // 校验名字的唯一性
        validateDemo02CategoryNameUnique(updateRequest.getId(), updateRequest.getParentId(), updateRequest.getName());

        // 更新
        Demo02CategoryEntity updateObj = BeanUtils.toBean(updateRequest, Demo02CategoryEntity.class);
        demo02CategoryMapper.updateById(updateObj);
    }

    @Override
    public void deleteDemo02Category(Long id) {
        // 校验存在
        validateDemo02CategoryExists(id);
        // 校验是否有子示例分类
        if (demo02CategoryMapper.selectCountByParentId(id) > 0) {
            throw exception(DEMO02_CATEGORY_EXITS_CHILDREN);
        }
        // 删除
        demo02CategoryMapper.deleteById(id);
    }

    private void validateDemo02CategoryExists(Long id) {
        if (demo02CategoryMapper.selectById(id) == null) {
            throw exception(DEMO02_CATEGORY_NOT_EXISTS);
        }
    }

    private void validateParentDemo02Category(Long id, Long parentId) {
        if (parentId == null || Demo02CategoryEntity.PARENT_ID_ROOT.equals(parentId)) {
            return;
        }
        // 1. 不能设置自己为父示例分类
        if (Objects.equals(id, parentId)) {
            throw exception(DEMO02_CATEGORY_PARENT_ERROR);
        }
        // 2. 父示例分类不存在
        Demo02CategoryEntity parentDemo02Category = demo02CategoryMapper.selectById(parentId);
        if (parentDemo02Category == null) {
            throw exception(DEMO02_CATEGORY_PARENT_NOT_EXITS);
        }
        // 3. 递归校验父示例分类，如果父示例分类是自己的子示例分类，则报错，避免形成环路
        if (id == null) { // id 为空，说明新增，不需要考虑环路
            return;
        }
        for (int i = 0; i < Short.MAX_VALUE; i++) {
            // 3.1 校验环路
            parentId = parentDemo02Category.getParentId();
            if (Objects.equals(id, parentId)) {
                throw exception(DEMO02_CATEGORY_PARENT_IS_CHILD);
            }
            // 3.2 继续递归下一级父示例分类
            if (parentId == null || Demo02CategoryEntity.PARENT_ID_ROOT.equals(parentId)) {
                break;
            }
            parentDemo02Category = demo02CategoryMapper.selectById(parentId);
            if (parentDemo02Category == null) {
                break;
            }
        }
    }

    private void validateDemo02CategoryNameUnique(Long id, Long parentId, String name) {
        Demo02CategoryEntity demo02Category = demo02CategoryMapper.selectByParentIdAndName(parentId, name);
        if (demo02Category == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的示例分类
        if (id == null) {
            throw exception(DEMO02_CATEGORY_NAME_DUPLICATE);
        }
        if (!Objects.equals(demo02Category.getId(), id)) {
            throw exception(DEMO02_CATEGORY_NAME_DUPLICATE);
        }
    }

    @Override
    public Demo02CategoryEntity getDemo02Category(Long id) {
        return demo02CategoryMapper.selectById(id);
    }

    @Override
    public List<Demo02CategoryEntity> getDemo02CategoryList(Demo02CategoryListRequest listRequest) {
        return demo02CategoryMapper.selectList(listRequest);
    }

}