package com.focela.platform.module.system.repository.mapper.department;

import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.system.controller.admin.department.dto.post.PostPageRequest;
import com.focela.platform.module.system.entity.department.PostEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface PostMapper extends BaseMapperX<PostEntity> {

    default List<PostEntity> selectList(Collection<Long> ids, Collection<Integer> statuses) {
        return selectList(new LambdaQueryWrapperX<PostEntity>()
                .inIfPresent(PostEntity::getId, ids)
                .inIfPresent(PostEntity::getStatus, statuses));
    }

    default PageResult<PostEntity> selectPage(PostPageRequest request) {
        return selectPage(request, new LambdaQueryWrapperX<PostEntity>()
                .likeIfPresent(PostEntity::getCode, request.getCode())
                .likeIfPresent(PostEntity::getName, request.getName())
                .eqIfPresent(PostEntity::getStatus, request.getStatus())
                .orderByDesc(PostEntity::getId));
    }

    default PostEntity selectByName(String name) {
        return selectOne(PostEntity::getName, name);
    }

    default PostEntity selectByCode(String code) {
        return selectOne(PostEntity::getCode, code);
    }

}
