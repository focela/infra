package com.focela.platform.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Documentation URLs.
 */
@Getter
@AllArgsConstructor
public enum DocumentEnum {

    REDIS_INSTALL("https://gitee.com/zhijiantianya/ruoyi-vue-pro/issues/I4VCSJ", "Redis installation docs"),
    TENANT("https://www.example.com", "SaaS multi-tenancy docs");

    private final String url;
    private final String memo;

}
