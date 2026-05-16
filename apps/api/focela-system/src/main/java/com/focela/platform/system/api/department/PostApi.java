package com.focela.platform.system.api.department;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.focela.platform.common.utils.collection.CollectionUtils;
import com.focela.platform.system.api.department.dto.PostRpcResponse;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Post API interface
 */
public interface PostApi {

    /**
     * Validate whether the posts are valid. The following cases are considered invalid:
     * 1. post ID does not exist
     * 2. post is disabled
     *
     * @param ids post IDs
     */
    void validatePostList(Collection<Long> ids);

    List<PostRpcResponse> getPostList(Collection<Long> ids);

    default Map<Long, PostRpcResponse> getPostMap(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return MapUtil.empty();
        }

        List<PostRpcResponse> list = getPostList(ids);
        return CollectionUtils.convertMap(list, PostRpcResponse::getId);
    }

}
