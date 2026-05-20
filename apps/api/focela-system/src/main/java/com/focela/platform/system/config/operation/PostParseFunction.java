package com.focela.platform.system.config.operation;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.system.domain.entity.department.PostEntity;
import com.focela.platform.system.service.department.PostService;
import com.mzt.logapi.service.IParseFunction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * {@link IParseFunction} implementation for post name
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PostParseFunction implements IParseFunction {

    public static final String NAME = "getPostById";

    private final PostService postService;

    @Override
    public String functionName() {
        return NAME;
    }

    @Override
    public String apply(Object value) {
        if (StrUtil.isEmptyIfStr(value)) {
            return "";
        }

        // get post information
        PostEntity post = postService.getPost(Convert.toLong(value));
        if (post == null) {
            log.warn("[apply][get post {{}} is empty", value);
            return "";
        }
        return post.getName();
    }

}
