package com.focela.platform.module.infra.repository.mapper.demo.demo02;

import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.infra.controller.admin.demo.demo02.vo.Demo02CategoryListReqVO;
import com.focela.platform.module.infra.repository.entity.demo.demo02.Demo02CategoryEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 示例分类 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface Demo02CategoryMapper extends BaseMapperX<Demo02CategoryEntity> {

    default List<Demo02CategoryEntity> selectList(Demo02CategoryListReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<Demo02CategoryEntity>()
                .likeIfPresent(Demo02CategoryEntity::getName, reqVO.getName())
                .eqIfPresent(Demo02CategoryEntity::getParentId, reqVO.getParentId())
                .betweenIfPresent(Demo02CategoryEntity::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(Demo02CategoryEntity::getId));
    }

    default Demo02CategoryEntity selectByParentIdAndName(Long parentId, String name) {
        return selectOne(Demo02CategoryEntity::getParentId, parentId, Demo02CategoryEntity::getName, name);
    }

    default Long selectCountByParentId(Long parentId) {
        return selectCount(Demo02CategoryEntity::getParentId, parentId);
    }

}