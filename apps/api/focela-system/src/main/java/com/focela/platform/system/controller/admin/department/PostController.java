package com.focela.platform.system.controller.admin.department;

import com.focela.platform.framework.apilog.core.annotation.ApiAccessLog;
import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.framework.common.model.PageParam;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.framework.excel.core.utils.ExcelUtils;
import com.focela.platform.system.controller.admin.department.dto.post.PostPageRequest;
import com.focela.platform.system.controller.admin.department.dto.post.PostResponse;
import com.focela.platform.system.controller.admin.department.dto.post.PostSaveRequest;
import com.focela.platform.system.controller.admin.department.dto.post.PostSimpleResponse;
import com.focela.platform.system.entity.department.PostEntity;
import com.focela.platform.system.service.department.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.focela.platform.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.focela.platform.framework.common.model.CommonResult.success;

@Tag(name = "Admin - Post")
@RestController
@RequestMapping("/system/post")
@Validated
public class PostController {

    @Resource
    private PostService postService;

    @PostMapping("/create")
    @Operation(summary = "create post")
    @PreAuthorize("@ss.hasPermission('system:post:create')")
    public CommonResult<Long> createPost(@Valid @RequestBody PostSaveRequest createRequest) {
        Long postId = postService.createPost(createRequest);
        return success(postId);
    }

    @PutMapping("/update")
    @Operation(summary = "update post")
    @PreAuthorize("@ss.hasPermission('system:post:update')")
    public CommonResult<Boolean> updatePost(@Valid @RequestBody PostSaveRequest updateRequest) {
        postService.updatePost(updateRequest);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "delete post")
    @PreAuthorize("@ss.hasPermission('system:post:delete')")
    public CommonResult<Boolean> deletePost(@RequestParam("id") Long id) {
        postService.deletePost(id);
        return success(true);
    }

    @DeleteMapping("delete-list")
    @Operation(summary = "batch delete post")
    @PreAuthorize("@ss.hasPermission('system:post:delete')")
    public CommonResult<Boolean> deletePostList(@RequestParam("ids") List<Long> ids) {
        postService.deletePostList(ids);
        return success(true);
    }

    @GetMapping(value = "/get")
    @Operation(summary = "get post info")
    @Parameter(name = "id", description = "Post ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:post:query')")
    public CommonResult<PostResponse> getPost(@RequestParam("id") Long id) {
        PostEntity post = postService.getPost(id);
        return success(BeanUtils.toBean(post, PostResponse.class));
    }

    @GetMapping(value = {"/list-all-simple", "simple-list"})
    @Operation(summary = "get post all list", description = "only include enabled post, for frontend dropdown options")
    public CommonResult<List<PostSimpleResponse>> getSimplePostList() {
        // get post list, only enabled ones
        List<PostEntity> list = postService.getPostList(null, Collections.singleton(CommonStatusEnum.ENABLE.getStatus()));
        // sort and return to frontend
        list.sort(Comparator.comparing(PostEntity::getSort));
        return success(BeanUtils.toBean(list, PostSimpleResponse.class));
    }

    @GetMapping("/page")
    @Operation(summary = "get post page list")
    @PreAuthorize("@ss.hasPermission('system:post:query')")
    public CommonResult<PageResult<PostResponse>> getPostPage(@Validated PostPageRequest pageRequest) {
        PageResult<PostEntity> pageResult = postService.getPostPage(pageRequest);
        return success(BeanUtils.toBean(pageResult, PostResponse.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "post management")
    @PreAuthorize("@ss.hasPermission('system:post:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void export(HttpServletResponse response, @Validated PostPageRequest request) throws IOException {
        request.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PostEntity> list = postService.getPostPage(request).getList();
        // output
        ExcelUtils.write(response, "Post Data.xls", "Post List", PostResponse.class,
                BeanUtils.toBean(list, PostResponse.class));
    }

}
