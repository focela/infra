package com.focela.platform.module.infra.service.demo.demo02;

import com.focela.platform.module.infra.controller.admin.demo.demo02.dto.Demo02CategoryListRequest;
import com.focela.platform.module.infra.controller.admin.demo.demo02.dto.Demo02CategorySaveRequest;
import com.focela.platform.module.infra.repository.entity.demo.demo02.Demo02CategoryEntity;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 示例分类 Service 接口
 *
 * @author 芋道源码
 */
public interface Demo02CategoryService {

    /**
     * 创建示例分类
     *
     * @param createRequest 创建信息
     * @return 编号
     */
    Long createDemo02Category(@Valid Demo02CategorySaveRequest createRequest);

    /**
     * 更新示例分类
     *
     * @param updateRequest 更新信息
     */
    void updateDemo02Category(@Valid Demo02CategorySaveRequest updateRequest);

    /**
     * 删除示例分类
     *
     * @param id 编号
     */
    void deleteDemo02Category(Long id);

    /**
     * 获得示例分类
     *
     * @param id 编号
     * @return 示例分类
     */
    Demo02CategoryEntity getDemo02Category(Long id);

    /**
     * 获得示例分类列表
     *
     * @param listRequest 查询条件
     * @return 示例分类列表
     */
    List<Demo02CategoryEntity> getDemo02CategoryList(Demo02CategoryListRequest listRequest);

}