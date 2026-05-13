package com.focela.platform.module.system.api.department;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.focela.platform.framework.common.utils.collection.CollectionUtils;
import com.focela.platform.module.system.api.department.dto.PostRpcResponse;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 岗位 API 接口
 */
public interface PostApi {

    /**
     * 校验岗位们是否有效。如下情况，视为无效：
     * 1. 岗位编号不存在
     * 2. 岗位被禁用
     *
     * @param ids 岗位编号数组
     */
    void validPostList(Collection<Long> ids);

    List<PostRpcResponse> getPostList(Collection<Long> ids);

    default Map<Long, PostRpcResponse> getPostMap(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return MapUtil.empty();
        }

        List<PostRpcResponse> list = getPostList(ids);
        return CollectionUtils.convertMap(list, PostRpcResponse::getId);
    }

}
