package com.focela.platform.framework.translate.core;

import cn.hutool.core.collection.CollUtil;
import com.fhs.core.trans.vo.VO;
import com.fhs.trans.service.impl.TransService;

import java.util.List;

/**
 * VO data translation utilities.
 */
public class TranslateUtils {

    private static TransService transService;

    public static void init(TransService transService) {
        TranslateUtils.transService = transService;
    }

    /**
     * Translate data.
     *
     * Use case: scenarios where the {@code @TransMethodResult} annotation cannot be applied,
     * so translation must be triggered manually.
     *
     * @param data data
     * @return translated result
     */
    public static <T extends VO> List<T> translate(List<T> data) {
        if (CollUtil.isNotEmpty((data))) {
            transService.transBatch(data);
        }
        return data;
    }

}
