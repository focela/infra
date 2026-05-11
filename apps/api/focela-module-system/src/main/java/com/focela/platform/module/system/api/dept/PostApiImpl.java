package com.focela.platform.module.system.api.dept;

import com.focela.platform.framework.common.util.object.BeanUtils;
import com.focela.platform.module.system.api.dept.dto.PostRespDTO;
import com.focela.platform.module.system.repository.entity.dept.PostEntity;
import com.focela.platform.module.system.service.dept.PostService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 岗位 API 实现类
 *
 * @author 芋道源码
 */
@Service
public class PostApiImpl implements PostApi {

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
