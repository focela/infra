package com.focela.platform.module.system.api.department;

import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.module.system.api.department.dto.PostRespDTO;
import com.focela.platform.module.system.repository.entity.department.PostEntity;
import com.focela.platform.module.system.service.department.PostService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 岗位 API 实现类
 */
@Service
public class LocalPostApi implements PostApi {

    @Resource
    private PostService postService;

    @Override
    public void validPostList(Collection<Long> ids) {
        postService.validatePostList(ids);
    }

    @Override
    public List<PostRespDTO> getPostList(Collection<Long> ids) {
        List<PostEntity> list = postService.getPostList(ids);
        return BeanUtils.toBean(list, PostRespDTO.class);
    }

}
