package com.focela.platform.system.service.department;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.system.controller.admin.department.request.post.PostPageRequest;
import com.focela.platform.system.controller.admin.department.request.post.PostSaveRequest;
import com.focela.platform.system.domain.entity.department.PostEntity;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * Post Service interface
 */
public interface PostService {

    /**
     * Create a post
     *
     * @param createRequest post information
     * @return post ID
     */
    Long createPost(PostSaveRequest createRequest);

    /**
     * Update a post
     *
     * @param updateRequest post information
     */
    void updatePost(PostSaveRequest updateRequest);

    /**
     * Delete a post
     *
     * @param id post ID
     */
    void deletePost(Long id);

    /**
     * Batch delete posts
     *
     * @param ids post ID array
     */
    void deletePostList(List<Long> ids);

    /**
     * Get the post list
     *
     * @param ids post ID array
     * @return post list
     */
    List<PostEntity> getPostList(@Nullable Collection<Long> ids);

    /**
     * Get the post list matching the criteria
     *
     * @param ids post ID array. If null, no filtering
     * @param statuses status array. If null, no filtering
     * @return post list
     */
    List<PostEntity> getPostList(@Nullable Collection<Long> ids,
                             @Nullable Collection<Integer> statuses);

    /**
     * Get the paginated post list
     *
     * @param request page query parameters
     * @return paginated post list
     */
    PageResult<PostEntity> getPostPage(PostPageRequest request);

    /**
     * Get post information
     *
     * @param id post ID
     * @return post information
     */
    PostEntity getPost(Long id);

    /**
     * Validate whether the posts are valid. The following are considered invalid:
     * 1. Post ID does not exist
     * 2. Post is disabled
     *
     * @param ids post ID array
     */
    void validatePostList(Collection<Long> ids);

}
