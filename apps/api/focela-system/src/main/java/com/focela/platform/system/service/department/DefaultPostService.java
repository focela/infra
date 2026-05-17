package com.focela.platform.system.service.department;

import cn.hutool.core.collection.CollUtil;
import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.controller.admin.department.dto.post.PostPageRequest;
import com.focela.platform.system.controller.admin.department.dto.post.PostSaveRequest;
import com.focela.platform.system.entity.department.PostEntity;
import com.focela.platform.system.repository.mapper.department.PostMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.common.utils.collection.CollectionUtils.convertMap;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.*;

/**
 * Post Service implementation class
 */
@Service
@Validated
@RequiredArgsConstructor
public class DefaultPostService implements PostService {

        private final PostMapper postMapper;

    @Override
    public Long createPost(PostSaveRequest createRequest) {
        // Validate
        validatePostForCreateOrUpdate(null, createRequest.getName(), createRequest.getCode());

        // Insert post
        PostEntity post = BeanUtils.toBean(createRequest, PostEntity.class);
        postMapper.insert(post);
        return post.getId();
    }

    @Override
    public void updatePost(PostSaveRequest updateRequest) {
        // Validate
        validatePostForCreateOrUpdate(updateRequest.getId(), updateRequest.getName(), updateRequest.getCode());

        // Update post
        PostEntity updateObj = BeanUtils.toBean(updateRequest, PostEntity.class);
        postMapper.updateById(updateObj);
    }

    @Override
    public void deletePost(Long id) {
        // Validate existence
        validatePostExists(id);
        // Delete post
        postMapper.deleteById(id);
    }

    @Override
    public void deletePostList(List<Long> ids) {
        postMapper.deleteByIds(ids);
    }

    private void validatePostForCreateOrUpdate(Long id, String name, String code) {
        // Validate that this entity exists
        validatePostExists(id);
        // Validate the uniqueness of the post name
        validatePostNameUnique(id, name);
        // Validate the uniqueness of the post code
        validatePostCodeUnique(id, code);
    }

    private void validatePostNameUnique(Long id, String name) {
        PostEntity post = postMapper.selectByName(name);
        if (post == null) {
            return;
        }
        // If id is null, no need to compare whether it is a post with the same id
        if (id == null) {
            throw exception(POST_NAME_DUPLICATE);
        }
        if (!post.getId().equals(id)) {
            throw exception(POST_NAME_DUPLICATE);
        }
    }

    private void validatePostCodeUnique(Long id, String code) {
        PostEntity post = postMapper.selectByCode(code);
        if (post == null) {
            return;
        }
        // If id is null, no need to compare whether it is a post with the same id
        if (id == null) {
            throw exception(POST_CODE_DUPLICATE);
        }
        if (!post.getId().equals(id)) {
            throw exception(POST_CODE_DUPLICATE);
        }
    }

    private void validatePostExists(Long id) {
        if (id == null) {
            return;
        }
        if (postMapper.selectById(id) == null) {
            throw exception(POST_NOT_FOUND);
        }
    }

    @Override
    public List<PostEntity> getPostList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return postMapper.selectByIds(ids);
    }

    @Override
    public List<PostEntity> getPostList(Collection<Long> ids, Collection<Integer> statuses) {
        return postMapper.selectList(ids, statuses);
    }

    @Override
    public PageResult<PostEntity> getPostPage(PostPageRequest request) {
        return postMapper.selectPage(request);
    }

    @Override
    public PostEntity getPost(Long id) {
        return postMapper.selectById(id);
    }

    @Override
    public void validatePostList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        // Get post information
        List<PostEntity> posts = postMapper.selectByIds(ids);
        Map<Long, PostEntity> postMap = convertMap(posts, PostEntity::getId);
        // Validate
        ids.forEach(id -> {
            PostEntity post = postMap.get(id);
            if (post == null) {
                throw exception(POST_NOT_FOUND);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(post.getStatus())) {
                throw exception(POST_NOT_ENABLE, post.getName());
            }
        });
    }
}
