package com.focela.platform.module.system.service.dept;

import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.common.util.collection.ArrayUtils;
import com.focela.platform.framework.test.core.ut.BaseDbUnitTest;
import com.focela.platform.module.system.controller.admin.dept.vo.post.PostPageReqVO;
import com.focela.platform.module.system.controller.admin.dept.vo.post.PostSaveReqVO;
import com.focela.platform.module.system.repository.entity.dept.PostEntity;
import com.focela.platform.module.system.repository.mapper.dept.PostMapper;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import jakarta.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static cn.hutool.core.util.RandomUtil.randomEle;
import static com.focela.platform.framework.common.util.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.framework.test.core.util.AssertUtils.assertPojoEquals;
import static com.focela.platform.framework.test.core.util.AssertUtils.assertServiceException;
import static com.focela.platform.framework.test.core.util.RandomUtils.*;
import static com.focela.platform.module.system.enums.ErrorCodeConstants.*;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link PostServiceImpl} 的单元测试类
 *
 * @author niudehua
 */
@Import(PostServiceImpl.class)
public class PostServiceImplTest extends BaseDbUnitTest {

    @Resource
    private PostServiceImpl postService;

    @Resource
    private PostMapper postMapper;

    @Test
    public void testCreatePost_success() {
        // 准备参数
        PostSaveReqVO reqVO = randomPojo(PostSaveReqVO.class,
                o -> o.setStatus(randomEle(CommonStatusEnum.values()).getStatus()))
                .setId(null); // 防止 id 被设置
        // 调用
        Long postId = postService.createPost(reqVO);

        // 断言
        assertNotNull(postId);
        // 校验记录的属性是否正确
        PostEntity post = postMapper.selectById(postId);
        assertPojoEquals(reqVO, post, "id");
    }

    @Test
    public void testUpdatePost_success() {
        // mock 数据
        PostEntity postDO = randomPostDO();
        postMapper.insert(postDO);// @Sql: 先插入出一条存在的数据
        // 准备参数
        PostSaveReqVO reqVO = randomPojo(PostSaveReqVO.class, o -> {
            // 设置更新的 ID
            o.setId(postDO.getId());
            o.setStatus(randomEle(CommonStatusEnum.values()).getStatus());
        });

        // 调用
        postService.updatePost(reqVO);
        // 校验是否更新正确
        PostEntity post = postMapper.selectById(reqVO.getId());
        assertPojoEquals(reqVO, post);
    }

    @Test
    public void testDeletePost_success() {
        // mock 数据
        PostEntity postDO = randomPostDO();
        postMapper.insert(postDO);
        // 准备参数
        Long id = postDO.getId();

        // 调用
        postService.deletePost(id);
        assertNull(postMapper.selectById(id));
    }

    @Test
    public void testValidatePost_notFoundForDelete() {
        // 准备参数
        Long id = randomLongId();

        // 调用, 并断言异常
        assertServiceException(() -> postService.deletePost(id), POST_NOT_FOUND);
    }

    @Test
    public void testValidatePost_nameDuplicateForCreate() {
        // mock 数据
        PostEntity postDO = randomPostDO();
        postMapper.insert(postDO);// @Sql: 先插入出一条存在的数据
        // 准备参数
        PostSaveReqVO reqVO = randomPojo(PostSaveReqVO.class,
            // 模拟 name 重复
            o -> o.setName(postDO.getName()));
        assertServiceException(() -> postService.createPost(reqVO), POST_NAME_DUPLICATE);
    }

    @Test
    public void testValidatePost_codeDuplicateForUpdate() {
        // mock 数据
        PostEntity postDO = randomPostDO();
        postMapper.insert(postDO);
        // mock 数据：稍后模拟重复它的 code
        PostEntity codePostDO = randomPostDO();
        postMapper.insert(codePostDO);
        // 准备参数
        PostSaveReqVO reqVO = randomPojo(PostSaveReqVO.class, o -> {
            // 设置更新的 ID
            o.setId(postDO.getId());
            // 模拟 code 重复
            o.setCode(codePostDO.getCode());
        });

        // 调用, 并断言异常
        assertServiceException(() -> postService.updatePost(reqVO), POST_CODE_DUPLICATE);
    }

    @Test
    public void testGetPostPage() {
        // mock 数据
        PostEntity postDO = randomPojo(PostEntity.class, o -> {
            o.setName("码仔");
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
        });
        postMapper.insert(postDO);
        // 测试 name 不匹配
        postMapper.insert(cloneIgnoreId(postDO, o -> o.setName("程序员")));
        // 测试 status 不匹配
        postMapper.insert(cloneIgnoreId(postDO, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));
        // 准备参数
        PostPageReqVO reqVO = new PostPageReqVO();
        reqVO.setName("码");
        reqVO.setStatus(CommonStatusEnum.ENABLE.getStatus());

        // 调用
        PageResult<PostEntity> pageResult = postService.getPostPage(reqVO);
        // 断言
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(postDO, pageResult.getList().get(0));
    }

    @Test
    public void testGetPostList() {
        // mock 数据
        PostEntity postDO01 = randomPojo(PostEntity.class);
        postMapper.insert(postDO01);
        // 测试 id 不匹配
        PostEntity postDO02 = randomPojo(PostEntity.class);
        postMapper.insert(postDO02);
        // 准备参数
        List<Long> ids = singletonList(postDO01.getId());

        // 调用
        List<PostEntity> list = postService.getPostList(ids);
        // 断言
        assertEquals(1, list.size());
        assertPojoEquals(postDO01, list.get(0));
    }

    @Test
    public void testGetPostList_idsAndStatus() {
        // mock 数据
        PostEntity postDO01 = randomPojo(PostEntity.class, o -> o.setStatus(CommonStatusEnum.ENABLE.getStatus()));
        postMapper.insert(postDO01);
        // 测试 status 不匹配
        PostEntity postDO02 = randomPojo(PostEntity.class, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus()));
        postMapper.insert(postDO02);
        // 准备参数
        List<Long> ids = Arrays.asList(postDO01.getId(), postDO02.getId());

        // 调用
        List<PostEntity> list = postService.getPostList(ids, singletonList(CommonStatusEnum.ENABLE.getStatus()));
        // 断言
        assertEquals(1, list.size());
        assertPojoEquals(postDO01, list.get(0));
    }

    @Test
    public void testGetPost() {
        // mock 数据
        PostEntity dbPostDO = randomPostDO();
        postMapper.insert(dbPostDO);
        // 准备参数
        Long id = dbPostDO.getId();
        // 调用
        PostEntity post = postService.getPost(id);
        // 断言
        assertNotNull(post);
        assertPojoEquals(dbPostDO, post);
    }

    @Test
    public void testValidatePostList_success() {
        // mock 数据
        PostEntity postDO = randomPostDO().setStatus(CommonStatusEnum.ENABLE.getStatus());
        postMapper.insert(postDO);
        // 准备参数
        List<Long> ids = singletonList(postDO.getId());

        // 调用，无需断言
        postService.validatePostList(ids);
    }

    @Test
    public void testValidatePostList_notFound() {
        // 准备参数
        List<Long> ids = singletonList(randomLongId());

        // 调用, 并断言异常
        assertServiceException(() -> postService.validatePostList(ids), POST_NOT_FOUND);
    }

    @Test
    public void testValidatePostList_notEnable() {
        // mock 数据
        PostEntity postDO = randomPostDO().setStatus(CommonStatusEnum.DISABLE.getStatus());
        postMapper.insert(postDO);
        // 准备参数
        List<Long> ids = singletonList(postDO.getId());

        // 调用, 并断言异常
        assertServiceException(() -> postService.validatePostList(ids), POST_NOT_ENABLE,
                postDO.getName());
    }

    @SafeVarargs
    private static PostEntity randomPostDO(Consumer<PostEntity>... consumers) {
        Consumer<PostEntity> consumer = (o) -> {
            o.setStatus(randomCommonStatus()); // 保证 status 的范围
        };
        return randomPojo(PostEntity.class, ArrayUtils.append(consumer, consumers));
    }

}
