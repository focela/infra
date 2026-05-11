package com.focela.platform.module.system.repository.mapper.dept;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.system.controller.admin.dept.vo.post.PostPageReqVO;
import com.focela.platform.module.system.repository.entity.dept.PostEntity;
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

    default PageResult<PostEntity> selectPage(PostPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PostEntity>()
                .likeIfPresent(PostEntity::getCode, reqVO.getCode())
                .likeIfPresent(PostEntity::getName, reqVO.getName())
                .eqIfPresent(PostEntity::getStatus, reqVO.getStatus())
                .orderByDesc(PostEntity::getId));
    }

    default PostEntity selectByName(String name) {
        return selectOne(PostEntity::getName, name);
    }

    default PostEntity selectByCode(String code) {
        return selectOne(PostEntity::getCode, code);
    }

}
