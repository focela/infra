package com.focela.platform.system.config.operatelog.core;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.system.entity.department.PostEntity;
import com.focela.platform.system.service.department.PostService;
import com.mzt.logapi.service.IParseFunction;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 岗位名字的 {@link IParseFunction} 实现类
 */
@Slf4j
@Component
public class PostParseFunction implements IParseFunction {

    public static final String NAME = "getPostById";

    @Resource
    private PostService postService;

    @Override
    public String functionName() {
        return NAME;
    }

    @Override
    public String apply(Object value) {
        if (StrUtil.isEmptyIfStr(value)) {
            return "";
        }

        // 获取岗位信息
        PostEntity post = postService.getPost(Convert.toLong(value));
        if (post == null) {
            log.warn("[apply][get post {{}}is empty", value);
            return "";
        }
        return post.getName();
    }

}
