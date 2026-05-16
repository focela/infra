package com.focela.platform.system.api.department;

import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.api.department.dto.PostRpcResponse;
import com.focela.platform.system.entity.department.PostEntity;
import com.focela.platform.system.service.department.PostService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * Post API implementation class
 */
@Service
public class LocalPostApi implements PostApi {

    @Resource
    private PostService postService;

    @Override
    public void validatePostList(Collection<Long> ids) {
        postService.validatePostList(ids);
    }

    @Override
    public List<PostRpcResponse> getPostList(Collection<Long> ids) {
        List<PostEntity> list = postService.getPostList(ids);
        return BeanUtils.toBean(list, PostRpcResponse.class);
    }

}
