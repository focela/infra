package com.focela.platform.module.system.repository.mapper.permission;

import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.system.controller.admin.permission.dto.menu.MenuListRequest;
import com.focela.platform.module.system.repository.entity.permission.MenuEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapperX<MenuEntity> {

    default MenuEntity selectByParentIdAndName(Long parentId, String name) {
        return selectOne(MenuEntity::getParentId, parentId, MenuEntity::getName, name);
    }

    default Long selectCountByParentId(Long parentId) {
        return selectCount(MenuEntity::getParentId, parentId);
    }

    default List<MenuEntity> selectList(MenuListRequest reqVO) {
        return selectList(new LambdaQueryWrapperX<MenuEntity>()
                .likeIfPresent(MenuEntity::getName, reqVO.getName())
                .eqIfPresent(MenuEntity::getStatus, reqVO.getStatus()));
    }

    default List<MenuEntity> selectListByPermission(String permission) {
        return selectList(MenuEntity::getPermission, permission);
    }

    default MenuEntity selectByComponentName(String componentName) {
        return selectOne(MenuEntity::getComponentName, componentName);
    }

}
