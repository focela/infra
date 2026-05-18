package com.focela.platform.system.service.department;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.collection.ArrayUtils;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.controller.admin.department.dto.post.PostPageRequest;
import com.focela.platform.system.controller.admin.department.dto.post.PostSaveRequest;
import com.focela.platform.system.entity.department.PostEntity;
import com.focela.platform.system.repository.mapper.department.PostMapper;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import jakarta.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static cn.hutool.core.util.RandomUtil.randomEle;
import static com.focela.platform.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.test.core.utils.RandomUtils.*;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.*;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link DefaultPostService}  unit test class
 */
@Import(DefaultPostService.class)
public class DefaultPostServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultPostService postService;

    @Resource
    private PostMapper postMapper;

    @Test
    public void testCreatePost_success() {
        // prepare parameters
        PostSaveRequest request = randomPojo(PostSaveRequest.class,
                o -> o.setStatus(randomEle(CommonStatusEnum.values()).getStatus()))
                .setId(null); // prevent id from being set
        // invoke
        Long postId = postService.createPost(request);

        // assert
        assertNotNull(postId);
        // verify record properties are correct
        PostEntity post = postMapper.selectById(postId);
        assertPojoEquals(request, post, "id");
    }

    @Test
    public void testUpdatePost_success() {
        // mock data
        PostEntity postEntity = randomPostEntity();
        postMapper.insert(postEntity);// @Sql: first insert an existing record
        // prepare parameters
        PostSaveRequest request = randomPojo(PostSaveRequest.class, o -> {
            // set updated ID
            o.setId(postEntity.getId());
            o.setStatus(randomEle(CommonStatusEnum.values()).getStatus());
        });

        // invoke
        postService.updatePost(request);
        // verify update is correct
        PostEntity post = postMapper.selectById(request.getId());
        assertPojoEquals(request, post);
    }

    @Test
    public void testDeletePost_success() {
        // mock data
        PostEntity postEntity = randomPostEntity();
        postMapper.insert(postEntity);
        // prepare parameters
        Long id = postEntity.getId();

        // invoke
        postService.deletePost(id);
        assertNull(postMapper.selectById(id));
    }

    @Test
    public void testValidatePost_notFoundForDelete() {
        // prepare parameters
        Long id = randomLongId();

        // invoke and assert exception
        assertServiceException(() -> postService.deletePost(id), POST_NOT_FOUND);
    }

    @Test
    public void testValidatePost_nameDuplicateForCreate() {
        // mock data
        PostEntity postEntity = randomPostEntity();
        postMapper.insert(postEntity);// @Sql: first insert an existing record
        // prepare parameters
        PostSaveRequest request = randomPojo(PostSaveRequest.class,
            // simulate duplicate name
            o -> o.setName(postEntity.getName()));
        assertServiceException(() -> postService.createPost(request), POST_NAME_DUPLICATE);
    }

    @Test
    public void testValidatePost_codeDuplicateForUpdate() {
        // mock data
        PostEntity postEntity = randomPostEntity();
        postMapper.insert(postEntity);
        // mock data: simulate duplicate code later
        PostEntity codePostEntity = randomPostEntity();
        postMapper.insert(codePostEntity);
        // prepare parameters
        PostSaveRequest request = randomPojo(PostSaveRequest.class, o -> {
            // set updated ID
            o.setId(postEntity.getId());
            // simulate duplicate code
            o.setCode(codePostEntity.getCode());
        });

        // invoke and assert exception
        assertServiceException(() -> postService.updatePost(request), POST_CODE_DUPLICATE);
    }

    @Test
    public void testGetPostPage() {
        // mock data
        PostEntity postEntity = randomPojo(PostEntity.class, o -> {
            o.setName("Coder");
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
        });
        postMapper.insert(postEntity);
        // test name mismatch
        postMapper.insert(cloneIgnoreId(postEntity, o -> o.setName("Programmer")));
        // test status mismatch
        postMapper.insert(cloneIgnoreId(postEntity, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));
        // prepare parameters
        PostPageRequest request = new PostPageRequest();
        request.setName("Coder");
        request.setStatus(CommonStatusEnum.ENABLE.getStatus());

        // invoke
        PageResult<PostEntity> pageResult = postService.getPostPage(request);
        // assert
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(postEntity, pageResult.getList().get(0));
    }

    @Test
    public void testGetPostList() {
        // mock data
        PostEntity postEntity01 = randomPojo(PostEntity.class);
        postMapper.insert(postEntity01);
        // test id mismatch
        PostEntity postEntity02 = randomPojo(PostEntity.class);
        postMapper.insert(postEntity02);
        // prepare parameters
        List<Long> ids = singletonList(postEntity01.getId());

        // invoke
        List<PostEntity> list = postService.getPostList(ids);
        // assert
        assertEquals(1, list.size());
        assertPojoEquals(postEntity01, list.get(0));
    }

    @Test
    public void testGetPostList_idsAndStatus() {
        // mock data
        PostEntity postEntity01 = randomPojo(PostEntity.class, o -> o.setStatus(CommonStatusEnum.ENABLE.getStatus()));
        postMapper.insert(postEntity01);
        // test status mismatch
        PostEntity postEntity02 = randomPojo(PostEntity.class, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus()));
        postMapper.insert(postEntity02);
        // prepare parameters
        List<Long> ids = Arrays.asList(postEntity01.getId(), postEntity02.getId());

        // invoke
        List<PostEntity> list = postService.getPostList(ids, singletonList(CommonStatusEnum.ENABLE.getStatus()));
        // assert
        assertEquals(1, list.size());
        assertPojoEquals(postEntity01, list.get(0));
    }

    @Test
    public void testGetPost() {
        // mock data
        PostEntity dbPostEntity = randomPostEntity();
        postMapper.insert(dbPostEntity);
        // prepare parameters
        Long id = dbPostEntity.getId();
        // invoke
        PostEntity post = postService.getPost(id);
        // assert
        assertNotNull(post);
        assertPojoEquals(dbPostEntity, post);
    }

    @Test
    public void testValidatePostList_success() {
        // mock data
        PostEntity postEntity = randomPostEntity().setStatus(CommonStatusEnum.ENABLE.getStatus());
        postMapper.insert(postEntity);
        // prepare parameters
        List<Long> ids = singletonList(postEntity.getId());

        // invoke, no assertion needed
        postService.validatePostList(ids);
    }

    @Test
    public void testValidatePostList_notFound() {
        // prepare parameters
        List<Long> ids = singletonList(randomLongId());

        // invoke and assert exception
        assertServiceException(() -> postService.validatePostList(ids), POST_NOT_FOUND);
    }

    @Test
    public void testValidatePostList_notEnable() {
        // mock data
        PostEntity postEntity = randomPostEntity().setStatus(CommonStatusEnum.DISABLE.getStatus());
        postMapper.insert(postEntity);
        // prepare parameters
        List<Long> ids = singletonList(postEntity.getId());

        // invoke and assert exception
        assertServiceException(() -> postService.validatePostList(ids), POST_NOT_ENABLE,
                postEntity.getName());
    }

    @SafeVarargs
    private static PostEntity randomPostEntity(Consumer<PostEntity>... consumers) {
        Consumer<PostEntity> consumer = (o) -> {
            o.setStatus(randomCommonStatus()); // ensure status range
        };
        return randomPojo(PostEntity.class, ArrayUtils.append(consumer, consumers));
    }

}
